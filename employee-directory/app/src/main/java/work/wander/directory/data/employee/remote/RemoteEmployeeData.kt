package work.wander.directory.data.employee.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import work.wander.directory.data.employee.room.EmployeeType

/**
 * Data class representing an employee.
 *
 * @property id The unique identifier for the employee. Defaults to a UUID of all zeros.
 * @property fullName The full name of the employee. Defaults to "Unspecified Name" if not provided.
 * @property phoneNumber The phone number of the employee.
 * @property emailAddress The email address of the employee. Defaults to "Unspecified Address" if not provided.
 * @property biography The biography of the employee.
 * @property photoUrlSmall The URL of the small version of the employee's photo.
 * @property photoUrlLarge The URL of the large version of the employee's photo.
 * @property team The team the employee belongs to. Defaults to "Unspecified Team" if not provided.
 * @property employeeType The type of the employee (FULL_TIME, PART_TIME, CONTRACTOR).
 */
@JsonClass(generateAdapter = true)
data class RemoteEmployeeData(
    @Json(name = "uuid") val id: String = DEFAULT_ID,
    @Json(name = "full_name") val fullName: String = "Unspecified Name",
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "email_address") val emailAddress: String = "Unspecified Address",
    @Json(name = "biography") val biography: String,
    @Json(name = "photo_url_small") val photoUrlSmall: String,
    @Json(name = "photo_url_large") val photoUrlLarge: String,
    @Json(name = "team") val team: String = "Unspecified Team",
    @Json(name = "employee_type") val employeeType: EmployeeType
) {
    companion object {
        const val DEFAULT_ID = "00000000-0000-0000-0000-000000000000"
    }
}


/**
 * Data class representing the structure of the "response" from the employee data API.
 */
@JsonClass(generateAdapter = true)
data class RemoteEmployeeDataResponseBody(
    val employees: List<RemoteEmployeeData>
)

/**
 * Custom JSON adapter for [EmployeeType] to handle the conversion between JSON and the enum.
 */
class EmployeeTypeJsonAdapter : JsonAdapter<EmployeeType>() {

    @FromJson
    override fun fromJson(reader: JsonReader): EmployeeType? {
        return if (reader.peek() != JsonReader.Token.NULL) {
            when (val string = reader.nextString()) {
                "FULL_TIME" -> EmployeeType.FULL_TIME
                "PART_TIME" -> EmployeeType.PART_TIME
                "CONTRACTOR" -> EmployeeType.CONTRACTOR
                else -> throw IllegalArgumentException("Unknown employee type: $string")
            }
        } else {
            reader.nextNull()
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: EmployeeType?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.name)
        }
    }

}
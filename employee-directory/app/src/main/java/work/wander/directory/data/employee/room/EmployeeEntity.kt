package work.wander.directory.data.employee.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an employee stored in the database.
 *
 * @param id The unique identifier for the employee.
 * @param fullName The full name of the employee.
 * @param phoneNumber The phone number of the employee.
 * @param emailAddress The email address of the employee.
 * @param biography The biography of the employee.
 * @param photoUrlSmall The URL of the small version of the employee's photo.
 * @param photoUrlLarge The URL of the large version of the employee's photo.
 * @param team The team the employee belongs to.
 * @param employeeType The type of the employee (FULL_TIME, PART_TIME, CONTRACTOR).
 *
 */
@Entity(
    tableName = "employees"
)
data class EmployeeEntity(
    @PrimaryKey @ColumnInfo("employee_id") val id: String,
    val fullName: String = "Unspecified Name",
    val phoneNumber: String,
    val emailAddress: String = "Unspecified Address",
    val biography: String,
    val photoUrlSmall: String,
    val photoUrlLarge: String,
    val team: String = "Unspecified Team",
    val employeeType: EmployeeType
)

/**
 * Enum class representing the type of an employee.
 */
enum class EmployeeType(
    val databaseId: String,
    val displayString: String
) {
    FULL_TIME("full_time", "Full-Time"),
    PART_TIME("part_time", "Part-Time"),
    CONTRACTOR("contractor", "Contractor"),
    UNDEFINED("undefined", "Unspecified"),
}

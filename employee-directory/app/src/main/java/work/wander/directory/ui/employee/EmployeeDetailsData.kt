package work.wander.directory.ui.employee

import work.wander.directory.data.employee.room.EmployeeEntity

data class EmployeeDetailsData(
    val id: String,
    val fullName: String,
    val biography: String,
    val teamText: String,
    val employeeTypeText: String,
    val photoUrl: String,
    val phoneNumber: String,
    val emailAddress: String,
    val employeeType: String,
) {
    companion object {

        fun getFormattedPhoneNumber(phoneNumber: String): String {
            return phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "($1) $2-$3")
        }
        fun fromEmployeeEntity(employeeEntity: EmployeeEntity): EmployeeDetailsData {
            return EmployeeDetailsData(
                id = employeeEntity.id,
                fullName = employeeEntity.fullName,
                biography = employeeEntity.biography,
                teamText = employeeEntity.team,
                employeeTypeText = employeeEntity.employeeType.displayString,
                photoUrl = employeeEntity.photoUrlLarge,
                phoneNumber = employeeEntity.phoneNumber,
                emailAddress = employeeEntity.emailAddress,
                employeeType = employeeEntity.employeeType.displayString,
            )
        }
    }
}

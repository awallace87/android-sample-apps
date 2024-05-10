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

package work.wander.directory.ui.directory

import work.wander.directory.data.employee.room.EmployeeEntity

/**
 * Data class for the row data of an employee.
 */
data class EmployeeRowData(
    val id: String,
    val fullName: String,
    val teamText: String,
    val employeeTypeText: String,
    val photoUrlSmall: String
) {
    companion object {

        fun fromEmployeeEntity(employeeEntity: EmployeeEntity): EmployeeRowData {
            return EmployeeRowData(
                id = employeeEntity.id,
                fullName = employeeEntity.fullName,
                teamText = employeeEntity.team,
                employeeTypeText = employeeEntity.employeeType.displayString,
                photoUrlSmall = employeeEntity.photoUrlSmall,
            )
        }
    }
}
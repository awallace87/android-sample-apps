package work.wander.directory.ui.directory

import work.wander.directory.data.employee.room.EmployeeEntity

/**
 * Data class representing a row of employee data in the directory.
 *
 * This class holds the data for a single row in the employee directory.
 * It includes the employee's ID, full name, team, employee type, and a small photo URL.
 *
 * The companion object provides a method to create an instance of this class from an EmployeeEntity.
 *
 * @property id The unique identifier of the employee.
 * @property fullName The full name of the employee.
 * @property teamText The team that the employee belongs to.
 * @property employeeTypeText The type of the employee (e.g., Full-time, Part-time).
 * @property photoUrlSmall The URL of the employee's small photo.
 */
data class EmployeeRowData(
    val id: String,
    val fullName: String,
    val teamText: String,
    val employeeTypeText: String,
    val photoUrlSmall: String
) {
    companion object {

        /**
         * Creates an instance of EmployeeRowData from an EmployeeEntity.
         *
         * This method takes an EmployeeEntity and extracts the necessary information to create an instance of EmployeeRowData.
         * It is used when converting data from the database format to the UI format.
         *
         * @param employeeEntity The EmployeeEntity to convert.
         * @return An instance of EmployeeRowData with the data from the EmployeeEntity.
         */
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
package work.wander.directory.data.employee.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Employee table in the Room database.
 *
 * This interface provides methods for querying the Employee table and inserting new rows into it.
 * Each method corresponds to a different SQL query.
 *
 * @see getAllEmployees for getting a Flow of all employees in the table.
 * @see getEmployeeById for getting a Flow of a single employee by their ID.
 * @see insertEmployee for inserting a single employee into the table.
 * @see insertEmployees for inserting multiple employees into the table.
 */
@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees")
    fun getAllEmployees() : Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE employee_id == :id")
    fun getEmployeeById(id: String): Flow<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmployee(employeeEntity: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmployees(employees: List<EmployeeEntity>)

    @Query("DELETE FROM employees")
    fun deleteAllEmployees()

}
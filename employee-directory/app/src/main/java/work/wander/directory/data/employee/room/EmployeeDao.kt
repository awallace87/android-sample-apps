package work.wander.directory.data.employee.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

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

}
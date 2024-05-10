package work.wander.directory.data.employee.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for storing employee data.
 *
 * This abstract class represents the Room database for the application. It includes a method for accessing the EmployeeDao,
 * which provides methods for querying the Employee table in the database.
 *
 * The @Database annotation indicates that this is a Room database and lists the entities (tables) in the database.
 * In this case, there is only one entity, EmployeeEntity.
 *
 * @see employeeDao for accessing the methods of the EmployeeDao.
 */
@Database(entities = [EmployeeEntity::class], version = 1)
abstract class EmployeeDatabase : RoomDatabase() {

    /**
     * Provides access to the EmployeeDao.
     *
     * This abstract method allows you to access the methods of the EmployeeDao, which provide ways to query the Employee table in the database.
     *
     * @return The EmployeeDao for querying the Employee table.
     */
    abstract fun employeeDao(): EmployeeDao
}
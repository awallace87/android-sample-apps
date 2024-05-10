package work.wander.directory.data.employee.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing dependencies related to the Room database for employee data.
 *
 * This module provides the EmployeeDatabase, which is the Room database for the application.
 *
 * @see providesEmployeeRoomDatabase for providing the EmployeeDatabase.
 */
@Module
@InstallIn(SingletonComponent::class)
class EmployeeRoomModule {

    @Provides
    @Singleton
    fun providesEmployeeRoomDatabase(
        @ApplicationContext context: Context
    ): EmployeeDatabase {
        return Room.databaseBuilder(context,
            EmployeeDatabase::class.java,
            "employee_db").build()
    }
}
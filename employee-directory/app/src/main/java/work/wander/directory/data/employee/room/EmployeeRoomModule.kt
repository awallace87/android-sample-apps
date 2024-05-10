package work.wander.directory.data.employee.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
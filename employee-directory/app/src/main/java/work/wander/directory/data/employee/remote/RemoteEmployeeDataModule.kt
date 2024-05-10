package work.wander.directory.data.employee.remote


import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


annotation class ForEmployeeRequest

/**
 * Provides module for employee data.

 */
@Module
@InstallIn(SingletonComponent::class)
class EmployeeDataModule {

    @Provides
    @Singleton
    @ForEmployeeRequest
    fun providesCoroutineScope(
        @ForEmployeeRequest coroutineDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    @Provides
    @Singleton
    @ForEmployeeRequest
    fun providesEmployeeRequestDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

/**
 * Binds module for employee data.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class EmployeeDataBindsModule {

    @Binds
    @Singleton
    abstract fun bindRemoteEmployeeDataSource(
        defaultRemoteEmployeeDataSource: DefaultRemoteEmployeeDataSource
    ): RemoteEmployeeDataSource

}
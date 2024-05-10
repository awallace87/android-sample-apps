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


/**
 * Annotation class for marking dependencies as being used for employee data requests.
 */
annotation class ForEmployeeRequest

/**
 * Dagger module for providing dependencies related to employee data.
 *
 * This module provides a CoroutineScope and a CoroutineDispatcher that are used for making requests for employee data.
 * The CoroutineScope is annotated with @ForEmployeeRequest to indicate that it should be used for employee data requests.
 * The CoroutineDispatcher is also annotated with @ForEmployeeRequest and is set to Dispatchers.IO, which is suitable for network requests.
 *
 * @see providesCoroutineScope for providing a CoroutineScope for employee data requests.
 * @see providesEmployeeRequestDispatcher for providing a CoroutineDispatcher for employee data requests.
 */
@Module
@InstallIn(SingletonComponent::class)
class RemoteEmployeeDataModule {

    /**
     * Provides a CoroutineScope for employee data requests.
     *
     * This CoroutineScope is annotated with @ForEmployeeRequest to indicate that it should be used for employee data requests.
     * It uses the provided CoroutineDispatcher (which should also be annotated with @ForEmployeeRequest) and a SupervisorJob.
     *
     * @param coroutineDispatcher The CoroutineDispatcher to use for the CoroutineScope. Should be annotated with @ForEmployeeRequest.
     * @return A CoroutineScope for employee data requests.
     */
    @Provides
    @Singleton
    @ForEmployeeRequest
    fun providesCoroutineScope(
        @ForEmployeeRequest coroutineDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    /**
     * Provides a CoroutineDispatcher for employee data requests.
     *
     * This CoroutineDispatcher is annotated with @ForEmployeeRequest to indicate that it should be used for employee data requests.
     * It is set to Dispatchers.IO, which is suitable for network requests.
     *
     * @return A CoroutineDispatcher for employee data requests.
     */
    @Provides
    @Singleton
    @ForEmployeeRequest
    fun providesEmployeeRequestDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

/**
 * Dagger module for binding interfaces to their implementations related to employee data.
 *
 * This module uses the @Binds annotation to tell Dagger which implementations to use for certain interfaces.
 * In this case, it binds the RemoteEmployeeDataSource interface to the DefaultRemoteEmployeeDataSource implementation.
 *
 * @see bindRemoteEmployeeDataSource for binding the RemoteEmployeeDataSource interface to the DefaultRemoteEmployeeDataSource implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteEmployeeDataBindsModule {

    @Binds
    @Singleton
    abstract fun bindRemoteEmployeeDataSource(
        defaultRemoteEmployeeDataSource: DefaultRemoteEmployeeDataSource
    ): RemoteEmployeeDataSource

}
package work.wander.directory.framework.logging

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dagger module for binding the AppLogger interface to its implementation.
 *
 * This module uses the @Binds annotation to tell Dagger which implementation to use for the AppLogger interface.
 * In this case, it binds the AppLogger interface to the TimberLogger implementation.
 *
 * @see bindLogger for binding the AppLogger interface to the TimberLogger implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingBindsModule {

    /**
     * Binds the AppLogger interface to the TimberLogger implementation.
     *
     * This method is abstract, as Dagger will provide the implementation.
     * The @Binds annotation tells Dagger that whenever it needs to provide an instance of AppLogger,
     * it should use an instance of TimberLogger.
     *
     * @param logger The TimberLogger implementation to bind to the AppLogger interface.
     * @return An instance of AppLogger.
     */
    @Binds
    abstract fun bindLogger(logger: TimberLogger): AppLogger
}
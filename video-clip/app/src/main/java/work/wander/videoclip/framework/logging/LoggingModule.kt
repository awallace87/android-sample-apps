package work.wander.videoclip.framework.logging

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingBindsModule {

    @Binds
    abstract fun bindLogger(logger: TimberLogger): AppLogger
}
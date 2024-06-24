package work.wander.funnyface.framework.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {

    @Binds
    @Singleton
    abstract fun bindNetworkStatusMonitor(
        defaultNetworkStatusMonitor: DefaultNetworkStatusMonitor
    ): NetworkStatusMonitor
}
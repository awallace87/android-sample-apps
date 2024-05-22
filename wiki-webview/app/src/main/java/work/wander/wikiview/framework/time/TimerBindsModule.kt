package work.wander.wikiview.framework.time

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerBindsModule {

    @Binds
    @Singleton
    abstract fun bindTimerManager(defaultTimerManager: DefaultTimerManager): TimerManager

}
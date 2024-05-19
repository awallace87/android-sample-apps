package work.wander.pomodogetter.framework.clock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ClockModule {

    @Binds
    abstract fun bindClock(clock: SystemClock): AppClock


}
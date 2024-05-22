package work.wander.pomodogetter.service.pomodoro

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PomodoroTimerServiceBindsModule {

    @Provides
    @Singleton
    fun providesPomodoroTimerServiceLauncher(
        @ApplicationContext context: Context
    ): PomodoroTimerService.Launcher {
        return PomodoroTimerService.Launcher(context)
    }
}
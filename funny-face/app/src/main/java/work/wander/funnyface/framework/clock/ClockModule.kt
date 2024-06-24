package work.wander.funnyface.framework.clock

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ClockModule {

    @Provides
    fun provideAppClock(): AppClock {
        return object : AppClock {

            override fun currentEpochTimeMillis(): Long {
                return System.currentTimeMillis()
            }

        }
    }


}
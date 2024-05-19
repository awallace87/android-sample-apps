package work.wander.pomogogetter.framework.clock

import javax.inject.Inject

interface AppClock {

    fun currentEpochTimeMillis(): Long

}

class SystemClock @Inject constructor(): AppClock {

    override fun currentEpochTimeMillis(): Long {
        return System.currentTimeMillis()
    }

}
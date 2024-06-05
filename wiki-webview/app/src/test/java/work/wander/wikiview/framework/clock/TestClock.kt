package work.wander.wikiview.framework.clock

class TestClock(private var currentTime: Long) : AppClock {

    override fun currentEpochTimeMillis(): Long {
        return currentTime
    }

    fun setTime(time: Long) {
        currentTime = time
    }
}
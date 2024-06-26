package work.wander.wikiview.framework.logging

import timber.log.Timber
import javax.inject.Inject

class TimberLogger @Inject constructor(): AppLogger {
    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun log(priority: Int, throwable: Throwable, message: String) {
        Timber.log(priority, throwable, message)
    }
}
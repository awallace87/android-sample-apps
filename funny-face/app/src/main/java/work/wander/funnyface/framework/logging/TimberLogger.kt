package work.wander.funnyface.framework.logging

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

// TODO Migrate to abstract class to enable implementation of other custom loggers with correct Tag
class DebugAppLoggerTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        var newTag: String = ""
        // Create a new Throwable to get the stack trace
        val throwable = Throwable()
        val stackTrace = throwable.stackTrace
        // Check if the stack trace is long enough
        if (stackTrace.size > CALLER_STACK_TRACE_DISTANCE) {
            // Get the calling class name
            newTag = stackTrace[CALLER_STACK_TRACE_DISTANCE].className ?: tag ?: ""
            // Remove the package name from the class name
            newTag = newTag.substring(newTag.lastIndexOf('.') + 1)
        }
        // Log with the new tag
        super.log(priority, newTag, message, t)
    }

    companion object {
        private const val CALLER_STACK_TRACE_DISTANCE = 7
    }
}
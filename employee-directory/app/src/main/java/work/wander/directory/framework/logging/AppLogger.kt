package work.wander.directory.framework.logging

import android.util.Log
import javax.inject.Inject

/**
 * Interface for application logging.
 *
 * This interface provides methods for logging messages at different priority levels.
 * The default priority level is DEBUG.
 *
 * @see log for logging a message with a specific priority level.
 * @see debug for logging a debug message.
 * @see info for logging an info message.
 * @see warn for logging a warning message.
 * @see error for logging an error message.
 */
interface AppLogger {
    fun log(priority: Int = Log.DEBUG, message: String)
    fun log(priority: Int = Log.DEBUG, throwable: Throwable, message: String)
    fun debug(message: String) = log(Log.DEBUG, message)
    fun debug(throwable: Throwable, message: String) = log(Log.DEBUG, throwable, message)
    fun info(message: String) = log(Log.INFO, message)
    fun info(throwable: Throwable, message: String) = log(Log.INFO, throwable, message)
    fun warn(message: String) = log(Log.WARN, message)
    fun warn(throwable: Throwable, message: String) = log(Log.WARN, throwable, message)
    fun error(message: String) = log(Log.ERROR, message)
    fun error(throwable: Throwable, message: String) = log(Log.ERROR, throwable, message)
}

/**
 * Logger implementation that uses the Android Log class for logging messages.
 *
 * This class implements the AppLogger interface and uses the Android Log class to log messages at different priority levels.
 * The tag for each log message is determined by the class name of the caller.
 *
 * @see log for logging a message with a specific priority level.
 * @see log for logging a message with a specific priority level and a throwable.
 */
class SystemAppLogger @Inject constructor() : AppLogger {
    override fun log(priority: Int, message: String) {
        Log.println(priority, getTagFromCaller(), message)
    }

    override fun log(priority: Int, throwable: Throwable, message: String) {
        Log.println(priority, getTagFromCaller(), "$message\n${Log.getStackTraceString(throwable)}")
    }

    companion object {
        fun getTagFromCaller(): String {
            val stackTrace = Thread.currentThread().stackTrace
            // TODO should define better search for caller
            val caller = stackTrace[4]
            return caller.className.substringAfterLast('.')
        }
    }
}


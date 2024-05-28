package work.wander.wikiview.framework.logging

import android.util.Log
import javax.inject.Inject

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


package work.wander.directory.framework.logging

import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of the AppLogger interface using Timber.
 *
 * This class provides an implementation of the AppLogger interface using the Timber library for logging.
 * It overrides the log methods from the AppLogger interface to log messages with Timber.
 *
 * The @Inject annotation tells Dagger that Dagger is responsible for creating instances of this class.
 *
 * @see log for logging a message with a specific priority level and an optional throwable.
 */
class TimberLogger @Inject constructor(): AppLogger {

    /**
     * Logs a message with a specific priority level.
     *
     * This method overrides the log method from the AppLogger interface.
     * It logs the message with the specified priority level using Timber.
     *
     * @param priority The priority level of the message.
     * @param message The message to log.
     */
    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    /**
     * Logs a message with a specific priority level and a throwable.
     *
     * This method overrides the log method from the AppLogger interface.
     * It logs the message and the throwable with the specified priority level using Timber.
     *
     * @param priority The priority level of the message.
     * @param throwable The throwable to log.
     * @param message The message to log.
     */
    override fun log(priority: Int, throwable: Throwable, message: String) {
        Timber.log(priority, throwable, message)
    }
}
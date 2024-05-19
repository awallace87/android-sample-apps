package work.wander.pomodogetter.framework.toast

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Interface to show toast messages.
 */
interface Toaster {

    /**
     * Show a toast message.
     *
     * @param message The message to be displayed.
     * @param duration The duration of the toast message.
     */
    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT)
}

/**
 * Default implementation of the [Toaster] interface.
 */
class DefaultToaster @Inject constructor(
    @ApplicationContext private val context: Context
) : Toaster {

    override fun showToast(message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }
}
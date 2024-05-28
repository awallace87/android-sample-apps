package work.wander.wikiview.framework.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * NetworkStatusMonitor is an interface that provides a method to check on network status.
 */
interface NetworkStatusMonitor {

    /**
     * Check if the device is connected to the internet
     */
    fun isInternetConnected(): Boolean

}

/**
 * DefaultNetworkStatusMonitor is an implementation of NetworkStatusMonitor that uses the ConnectivityManager to check for network status.
 */
class DefaultNetworkStatusMonitor @Inject constructor(
    @ApplicationContext private val context: Context
): NetworkStatusMonitor {

    override fun isInternetConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

}

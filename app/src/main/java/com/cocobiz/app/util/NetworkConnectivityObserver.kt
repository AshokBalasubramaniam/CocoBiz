package com.cocobiz.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // StateFlow for banner UI (true/false)
    private val _isConnected = MutableStateFlow(isCurrentlyConnected())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // SharedFlow — emits Unit every time internet becomes available.
    // replay=1 means new collectors (repositories on init) get last event immediately.
    private val _onConnected = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 64)
    val onConnected: Flow<Unit> = _onConnected

    init {
        // Emit immediately if already online so repositories refresh on first collect
        if (isCurrentlyConnected()) _onConnected.tryEmit(Unit)

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
                _onConnected.tryEmit(Unit)   // Always emit on reconnect
            }

            override fun onLost(network: Network) {
                _isConnected.value = isCurrentlyConnected()
            }

            override fun onUnavailable() {
                _isConnected.value = false
            }
        })
    }

    private fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

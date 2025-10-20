package com.yaropaul.chatbotaiassistant.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.yaropaul.chatbotaiassistant.domain.repository.IConnectivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Implementation of IConnectivityRepository interface.
 * Handles network connectivity monitoring using Android's ConnectivityManager.
 *
 * Benefits:
 * - Implements interface from domain layer (dependency inversion)
 * - Encapsulates Android framework dependencies in data layer
 * - Provides reactive connectivity updates via Flow
 * - Easy to test with mocks
 * - Properly handles callback lifecycle with callbackFlow
 */
class ConnectivityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IConnectivityRepository {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Observes network connectivity changes as a Flow.
     * Uses callbackFlow to convert callback-based API to Flow.
     * Properly unregisters callback when Flow is cancelled.
     */
    override fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        // Send initial connectivity state
        trySend(isConnected())

        // Register callback for updates
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Unregister callback when Flow is cancelled (prevents memory leaks)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    /**
     * Checks current connectivity status synchronously.
     */
    override suspend fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

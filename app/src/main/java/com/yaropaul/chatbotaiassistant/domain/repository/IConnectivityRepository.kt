package com.yaropaul.chatbotaiassistant.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for network connectivity monitoring.
 * This belongs to the domain layer and defines the contract.
 * Data layer will provide the implementation.
 */
interface IConnectivityRepository {

    /**
     * Observes network connectivity status as a Flow.
     * @return Flow emitting true when connected, false when disconnected
     */
    fun observeConnectivity(): Flow<Boolean>

    /**
     * Checks current connectivity status (one-time check).
     * @return true if connected, false otherwise
     */
    suspend fun isConnected(): Boolean
}

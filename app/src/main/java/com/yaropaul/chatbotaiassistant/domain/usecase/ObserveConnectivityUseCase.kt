package com.yaropaul.chatbotaiassistant.domain.usecase

import com.yaropaul.chatbotaiassistant.domain.repository.IConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing network connectivity.
 * Encapsulates the business logic for monitoring network status.
 *
 * This follows the Single Responsibility Principle and makes testing easier.
 */
class ObserveConnectivityUseCase @Inject constructor(
    private val connectivityRepository: IConnectivityRepository
) {
    /**
     * Observes network connectivity status as a Flow.
     * The UI will automatically update when connectivity changes.
     *
     * @return Flow<Boolean> emitting true when connected, false when disconnected
     */
    operator fun invoke(): Flow<Boolean> {
        return connectivityRepository.observeConnectivity()
    }
}

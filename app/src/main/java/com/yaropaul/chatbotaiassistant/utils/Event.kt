package com.yaropaul.chatbotaiassistant.utils

/**
 * Wrapper class for handling one-time events (like error messages).
 */
open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its reuse.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the contents, even if they have already been processed.
     */
    fun peekContent(): T = content
}
package com.yaropaul.chatbotaiassistant.utils

import com.yaropaul.chatbotaiassistant.BuildConfig

object Constants {
    /**
     * OpenAI API Key loaded from local.properties via BuildConfig
     * This prevents the key from being hardcoded in version control
     */
    val API_KEY: String = BuildConfig.OPENAI_API_KEY

    const val BASE_URL = "https://api.openai.com/v1/"
    const val TIMEOUT = 60L
}
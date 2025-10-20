package com.yaropaul.chatbotaiassistant.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yaropaul.chatbotaiassistant.data.apiRemote.ApiService
import com.yaropaul.chatbotaiassistant.data.local.AppDatabase
import com.yaropaul.chatbotaiassistant.data.local.MessageDao
import com.yaropaul.chatbotaiassistant.data.repository.ChatRepositoryImpl
import com.yaropaul.chatbotaiassistant.data.repository.ConnectivityRepositoryImpl
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.repository.IConnectivityRepository
import com.yaropaul.chatbotaiassistant.utils.Constants.BASE_URL
import com.yaropaul.chatbotaiassistant.utils.Constants.TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Refactored Hilt DI module following Clean Architecture principles.
 *
 * Key Improvements:
 * 1. Provides interfaces instead of concrete implementations (Dependency Inversion)
 * 2. Separates repository implementations into data layer
 * 3. Uses @ApplicationContext for Context injection
 * 4. Clear dependency graph: UI -> Domain (interfaces) <- Data (implementations)
 * 5. Easy to swap implementations for testing
 * 6. UseCases are automatically injected (no need to provide them)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ============ Network Layer ============

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // ============ Database Layer ============

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "chat_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    // ============ Repository Layer (Interfaces) ============

    /**
     * Provides IChatRepository interface implementation.
     * This follows Dependency Inversion Principle:
     * - High-level modules (UseCases) depend on abstraction (IChatRepository)
     * - Low-level modules (ChatRepositoryImpl) implement the abstraction
     */
    @Provides
    @Singleton
    fun provideChatRepository(
        apiService: ApiService,
        messageDao: MessageDao
    ): IChatRepository {
        return ChatRepositoryImpl(
            apiService = apiService,
            messageDao = messageDao,
            currentUserId = "1" // TODO: Get from user session/preferences
        )
    }

    /**
     * Provides IConnectivityRepository interface implementation.
     * Encapsulates Android framework dependencies in data layer.
     */
    @Provides
    @Singleton
    fun provideConnectivityRepository(
        @ApplicationContext context: Context
    ): IConnectivityRepository {
        return ConnectivityRepositoryImpl(context)
    }

    // ============ UseCases ============
    // No need to provide UseCases explicitly!
    // Hilt automatically injects them because they have @Inject constructors.
    // This is cleaner and reduces boilerplate.
}
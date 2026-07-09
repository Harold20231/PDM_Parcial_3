package com.example.rankeuca.di

import android.content.Context
import com.example.rankeuca.data.api.RankeUcaApi
import com.example.rankeuca.data.api.RankeUcaApiImpl
import com.example.rankeuca.data.database.AppDatabase
import com.example.rankeuca.data.database.dao.OptionDao
import com.example.rankeuca.data.database.dao.QuestionDao
import com.example.rankeuca.data.repositories.QuestionRepository
import com.example.rankeuca.data.repositories.QuestionRepositoryImpl
import com.example.rankeuca.data.repositories.VoteRepository
import com.example.rankeuca.data.repositories.VoteRepositoryImpl
import com.example.rankeuca.utils.ApiKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    @Provides
    @Singleton
    fun provideRankeUcaApi(client: HttpClient): RankeUcaApi {
        return RankeUcaApiImpl(client)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideQuestionDao(database: AppDatabase): QuestionDao {
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun provideOptionDao(database: AppDatabase): OptionDao {
        return database.optionDao()
    }

    @Provides
    @Singleton
    fun provideApiKeyManager(@ApplicationContext context: Context): ApiKeyManager {
        return ApiKeyManager(context)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        questionDao: QuestionDao,
        optionDao: OptionDao,
        api: RankeUcaApi
    ): QuestionRepository {
        return QuestionRepositoryImpl(questionDao, optionDao, api)
    }

    @Provides
    @Singleton
    fun provideVoteRepository(
        optionDao: OptionDao,
        api: RankeUcaApi
    ): VoteRepository {
        return VoteRepositoryImpl(optionDao, api)
    }
}
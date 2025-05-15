package com.blackcube.remote.di

import com.blackcube.remote.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sessionManager: com.blackcube.authorization.session.SessionManager
    ): Interceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val path = original.url.encodedPath

        // Если это регистрация или логин — пропускаем без токена
        if (path == "/auth/register" || path == "/auth/login") {
            chain.proceed(original)
        } else {
            val token: String? = runBlocking { sessionManager.getToken() }

            val requestWithToken = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else original

            chain.proceed(requestWithToken)
        }
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(provideLoggingInterceptor())
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideMainRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://192.168.0.13:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
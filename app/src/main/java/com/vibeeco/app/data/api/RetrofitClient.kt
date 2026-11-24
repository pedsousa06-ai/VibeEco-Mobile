package com.vibeeco.app.data.api

import com.google.gson.GsonBuilder
import com.vibeeco.app.data.AppConfig
import com.vibeeco.app.data.Session
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // envia o JWT em todas as chamadas e detecta token recusado pela API
                val original = chain.request()
                val token = Session.token
                val requisicao = if (token != null && original.header("Authorization") == null)
                    original.newBuilder().header("Authorization", "Bearer $token").build()
                else original
                val resposta = chain.proceed(requisicao)
                if (token != null && resposta.code == 401 &&
                    resposta.peekBody(2048).string().contains("TOKEN_INVALIDO")
                ) {
                    Session.expirada = true
                }
                resposta
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(AppConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AppConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val vibeApi: VibeApiService by lazy {
        retrofit.create(VibeApiService::class.java)
    }
}
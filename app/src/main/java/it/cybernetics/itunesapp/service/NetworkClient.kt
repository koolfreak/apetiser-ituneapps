package it.cybernetics.itunesapp.service

import it.cybernetics.itunesapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


class NetworkClient {

    companion object {
        // development endpoint
        // production endpoint
        val BASE_URL = "https://itunes.apple.com/"

        fun <T> createRxService(clazz: Class<T>): T {

            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            val httpClient = OkHttpClient.Builder()
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .cache(null)

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                httpClient.addInterceptor(logging)
            }

            builder.client(httpClient.build())
            val retrofit = builder.build()
            return retrofit.create(clazz)
        }

    }
}
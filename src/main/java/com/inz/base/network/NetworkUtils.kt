package com.inz.base.network

import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Suppress("unused")
class NetworkUtils(private val baseUrl: String) {

    companion object {
        private const val TAG = "NetworkUtils"
        private val instanceMap: MutableMap<String, NetworkUtils> = mutableMapOf()

        fun getInstance(baseUrl: String): NetworkUtils {
            synchronized(NetworkUtils::class) {
                var networkUtils = instanceMap[baseUrl]
                if (networkUtils == null) {
                    networkUtils = NetworkUtils(baseUrl)
                }
                return networkUtils
            }
        }
    }

    private var retrofit: Retrofit? = null

    interface InitCallback {

        fun createOkhttpClientBuilder(): ClientBuilder

        fun createRetrofitBuilder(baseUrl: String): RetrofitBuilder
    }

    fun initClient(callback: InitCallback) {
        val clientBuilder = callback.createOkhttpClientBuilder()
        val retrofitBuilder = callback.createRetrofitBuilder(baseUrl)
        val retrofit = retrofitBuilder.setClient(clientBuilder.build()).build()
        this.retrofit = retrofit
        instanceMap[baseUrl] = this
    }

    fun <T> getRetrofitInterface(clazz: Class<T>): T? {
        return retrofit?.create(clazz)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class ClientBuilder {
        private val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        private var callback: BuildCallback? = null

        interface BuildCallback {
            fun updateConfig(builder: OkHttpClient.Builder)
        }

        private companion object {
            private const val DEFAULT_TIME_OUT_DURATION = 5000L
        }

        init {
            setTimeOut(DEFAULT_TIME_OUT_DURATION)
        }

        fun addInterceptor(interceptor: Interceptor): ClientBuilder {
            okHttpClientBuilder.addInterceptor(interceptor)
            return this
        }

        fun addNetworkInterceptor(networkInterceptor: Interceptor): ClientBuilder {
            okHttpClientBuilder.addNetworkInterceptor(networkInterceptor)
            return this
        }

        fun setTimeOut(duration: Long): ClientBuilder {
            okHttpClientBuilder.callTimeout(duration, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.connectTimeout(duration, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.readTimeout(duration, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.writeTimeout(duration, TimeUnit.MILLISECONDS)
            return this
        }

        fun setUpdateConfigCallback(callback: BuildCallback): ClientBuilder {
            this.callback = callback
            return this
        }

        fun build(): OkHttpClient {
            callback?.updateConfig(this.okHttpClientBuilder)
            return this.okHttpClientBuilder.build()
        }
    }

    class RetrofitBuilder(baseUrl: String) {
        private val retrofitBuilder = Retrofit.Builder()
        private var callback: BuildCallback? = null

        interface BuildCallback {
            fun updateConfig(builder: Retrofit.Builder)
        }

        init {
            retrofitBuilder.baseUrl(baseUrl)
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create(Gson()))
            retrofitBuilder.addCallAdapterFactory(
                RxJava2CallAdapterFactory.createWithScheduler(
                    Schedulers.io()
                )
            )

        }

        fun setClient(client: OkHttpClient): RetrofitBuilder {
            retrofitBuilder.client(client)
            return this
        }

        fun addConverterFactory(converterFactory: Converter.Factory): RetrofitBuilder {
            retrofitBuilder.addConverterFactory(converterFactory)
            return this
        }

        fun addCallAdapterFactory(callAdapterFactory: CallAdapter.Factory): RetrofitBuilder {
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory)
            return this
        }

        fun setCallback(callback: BuildCallback): RetrofitBuilder {
            this.callback = callback
            return this
        }

        fun build(): Retrofit {
            callback?.updateConfig(this.retrofitBuilder)
            return this.retrofitBuilder.build()
        }
    }

}
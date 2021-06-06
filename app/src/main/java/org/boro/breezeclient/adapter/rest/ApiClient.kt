package org.boro.breezeclient.adapter.rest

import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.GsonBuilder
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.boro.breezeclient.domain.PeakFlow
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.Instant

interface ApiClient {

    @POST("api/peak-flows")
    fun create(@Body peakFlow: PeakFlow): Completable

    @GET("api/peak-flows")
    fun getAll(): Observable<List<PeakFlow>>

    @PUT("api/peak-flows/{id}")
    fun update(@Path("id") id: Long, @Body request: ApiRequest): Completable

    @DELETE("/api/peak-flows/{id}")
    fun delete(@Path("id") id: Long): Completable

    companion object {
        fun create(): ApiClient {
            val gson = Converters.registerAll(GsonBuilder())
                .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://192.168.0.22:9191/")
                .client(client)
                .build()
                .create(ApiClient::class.java)
        }
    }
}

data class ApiRequest(
    var value: Int,
    var checkedAt: Instant,
)
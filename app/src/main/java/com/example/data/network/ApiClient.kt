package com.example.data.network

import com.example.data.model.MotherPanelAddOrderResponse
import com.example.data.model.MotherPanelBalanceResponse
import com.example.data.model.MotherPanelServiceResponse
import com.example.data.model.MotherPanelStatusResponse
import com.example.data.model.UddoktaPayCreateResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface MotherPanelApi {
    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getServices(
        @Field("key") key: String,
        @Field("action") action: String = "services"
    ): List<MotherPanelServiceResponse>

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun addOrder(
        @Field("key") key: String,
        @Field("action") action: String = "add",
        @Field("service") service: Int,
        @Field("link") link: String,
        @Field("quantity") quantity: Int
    ): MotherPanelAddOrderResponse

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getOrderStatus(
        @Field("key") key: String,
        @Field("action") action: String = "status",
        @Field("order") order: Long
    ): MotherPanelStatusResponse

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getBalance(
        @Field("key") key: String,
        @Field("action") action: String = "balance"
    ): MotherPanelBalanceResponse
}

interface PaymentlyApi {
    @POST("checkout-v2")
    suspend fun createCheckout(
        @Header("RT-UDDOKTAPAY-API-KEY") apiKey: String,
        @Body requestBody: RequestBody
    ): UddoktaPayCreateResponse
}

object NetworkClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val motherPanelService: MotherPanelApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://motherpanel.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MotherPanelApi::class.java)
    }

    val paymentlyService: PaymentlyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://goribersmm.paymently.io/api/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PaymentlyApi::class.java)
    }
}

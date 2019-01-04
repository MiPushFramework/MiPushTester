package moe.yuuta.mipushtester.api

import androidx.annotation.NonNull
import com.google.gson.JsonObject
import moe.yuuta.common.Constants
import moe.yuuta.mipushtester.BuildConfig
import moe.yuuta.mipushtester.push.PushRequest
import moe.yuuta.mipushtester.topic.Topic
import moe.yuuta.mipushtester.update.Update
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIManager {
    private val apiInterface: APIInterface

    init {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                .addInterceptor(object: Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val orig: Request = chain.request()

                        val builder: Request.Builder =
                                orig.newBuilder()
                                        .addHeader(Constants.HEADER_VERSION, BuildConfig.VERSION_NAME)
                                        .addHeader(Constants.HEADER_PRODUCT, BuildConfig.APPLICATION_ID)
                        val request: Request = builder.build()
                        return chain.proceed(request)
                    }
                })
        apiInterface = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SERVER_URL)
                .client(builder.build())
                .build()
                .create(APIInterface::class.java)
    }

    fun push(@NonNull request: PushRequest): Call<JsonObject> =
            apiInterface.push(request)

    fun getUpdate(): Call<Update> =
            apiInterface.getUpdate()

    fun getAvailableTopics(): Call<MutableList<Topic>> =
            apiInterface.getAvailableTopics()
}
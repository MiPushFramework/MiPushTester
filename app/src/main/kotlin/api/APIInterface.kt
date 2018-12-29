package moe.yuuta.mipushtester.api

import com.google.gson.JsonObject
import moe.yuuta.mipushtester.push.PushRequest
import moe.yuuta.mipushtester.topic.Topic
import moe.yuuta.mipushtester.update.Update
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIInterface {
    @POST("/test")
    fun push(@Body request: PushRequest): Call<JsonObject>

    @GET("/update")
    fun getUpdate(): Call<Update>

    @GET("/test/topic")
    fun getAvailableTopics(): Call<MutableList<Topic>>
}
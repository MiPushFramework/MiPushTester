package moe.yuuta.mipushtester.topic

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Topic(@SerializedName(value = "title") val title: String,
                 @SerializedName(value = "description") val description: String,
                 @SerializedName(value = "id") val id: String,
                 @Expose var subscribed: Boolean)
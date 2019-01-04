package moe.yuuta.mipushtester.update

import com.google.gson.annotations.SerializedName

data class Update(@SerializedName("version_name") val versionName: String,
                  @SerializedName("version_code") val versionCode: Int,
                  @SerializedName("html_link") val htmlLink: String)
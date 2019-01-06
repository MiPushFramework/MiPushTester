package moe.yuuta.server.api.update

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class Update(@get:JsonProperty("version_name") var versionName: String = "",
                  @get:JsonProperty("version_code") var versionCode: Int = -1,
                  @get:JsonProperty("html_link") var htmlLink: String = "")

package moe.yuuta.server.mipush

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMessageResponse(
        @JsonProperty("result") var result: String,
        @JsonProperty("description") var description: String,
        @JsonProperty("data") var data: SendMessageResponse.Data,
        @JsonProperty("code") var code: Int,
        @JsonProperty("info") var info: String) {
    companion object {
        const val RESULT_OK = "ok"
        const val RESULT_ERROR = "error"

        const val CODE_SUCCESS = 0
    }
    class Data {
        private val id = ""
    }
}

package moe.yuuta.server.mipush

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.RequestOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import moe.yuuta.common.Constants
import moe.yuuta.server.formprocessor.HttpForm

// TODO: Add tests
open class MiPushApi(private val httpClient: HttpClient?) {
    companion object {
        const val HOST_CHINA = "api.xmpush.xiaomi.com"
        const val HOST_GLOBAL = "api.xmpush.global.xiaomi.com"

        @JvmStatic
        private fun buildExtras(customExtras: Map<String, String>): String {
            val extrasBuilder = StringBuilder()
            for (key in customExtras.keys) {
                extrasBuilder.append("extra.")
                extrasBuilder.append(key)
                extrasBuilder.append("=")
                extrasBuilder.append(customExtras.get(key))
                extrasBuilder.append("&")
            }
            var extras = extrasBuilder.toString()
            extras = extras.substring(0, extras.length - 1)
            return extras
        }
    }

    open fun pushOnce(message: Message, regId: String, regIdType: Int, customExtras: Map<String, String>?, useGlobal: Boolean, handler: Handler<AsyncResult<HttpResponse<SendMessageResponse>>>) {
        val apiUrl: String = when (regIdType) {
            Constants.REG_ID_TYPE_REG_ID -> {
                message.regId = regId
                "/v3/message/regid"
            }
            Constants.REG_ID_TYPE_ACCOUNT -> {
                message.account = regId
                "/v2/message/user_account"
            }
            Constants.REG_ID_TYPE_ALIAS -> {
                message.alias = regId
                "/v3/message/alias"
            }
            else -> "/v3/message/regid"
        }
        val arguments = HttpForm.toBuffer(message)
        if (customExtras != null) {
            arguments.appendString("&" + buildExtras(customExtras))
        }
        LoggerFactory.getLogger(MiPushApi::class.java).error("Sending to $apiUrl , regIdWithType=$regId, $regIdType")
        LoggerFactory.getLogger(MiPushApi::class.java).error(arguments.toString())
        generateHttpCall(HttpMethod.POST, apiUrl, useGlobal)
                .`as`(BodyCodec.json(SendMessageResponse::class.java))
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .sendBuffer(arguments, handler)
    }

    fun pushOnceToTopic (message: Message, topic: String, customExtras: Map<String, String>?, useGlobal: Boolean, handler: Handler<AsyncResult<HttpResponse<SendMessageResponse>>>) {
        val arguments = HttpForm.toBuffer(message)
        arguments.appendString("&topic=$topic")
        if (customExtras != null) {
            arguments.appendString("&" + buildExtras(customExtras))
        }
        generateHttpCall(HttpMethod.POST, "/v3/message/topic", useGlobal)
                .`as`(BodyCodec.json(SendMessageResponse::class.java))
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .sendBuffer(arguments, handler)
    }

    private fun generateHttpCall (method: HttpMethod, path: String, useGlobal: Boolean): HttpRequest<Buffer> {
        val webClient = WebClient.wrap(httpClient)
        return webClient.request(method, RequestOptions()
                                        .setPort(443)
                                        .setHost(if (useGlobal) HOST_GLOBAL else HOST_CHINA)
                                        .setSsl(true)
                                        .setURI(path))
                .putHeader("Authorization", "key=" + System.getenv("MIPUSH_AUTH"))
    }
}

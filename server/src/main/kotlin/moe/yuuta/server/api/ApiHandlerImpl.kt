package moe.yuuta.server.api

import io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine
import moe.yuuta.common.Constants
import moe.yuuta.common.Constants.DISPLAY_LIGHTS
import moe.yuuta.common.Constants.DISPLAY_SOUND
import moe.yuuta.common.Constants.DISPLAY_VIBRATE
import moe.yuuta.server.api.ApiUtils.separateListToComma
import moe.yuuta.server.api.update.Update
import moe.yuuta.server.dataverify.DataVerifier
import moe.yuuta.server.github.GitHubApi
import moe.yuuta.server.mipush.Message
import moe.yuuta.server.mipush.MiPushApi
import moe.yuuta.server.mipush.SendMessageResponse
import moe.yuuta.server.res.Resources
import moe.yuuta.server.topic.TopicRegistry
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

open class ApiHandlerImpl(private val vertx: Vertx?) : ApiHandler {
    private val logger = LoggerFactory.getLogger(ApiHandlerImpl::class.simpleName)

    @Override
    override fun handleFrameworkIndex(routingContext: RoutingContext) {
        handleAnyIndex(true, routingContext)
    }

    private fun handleAnyIndex(isFramework: Boolean, routingContext: RoutingContext) {
        val response = routingContext.response()
        if (response.ended() || response.closed()) {
            return
        }
        var locale: Locale = Resources.getRequestLocale(routingContext.preferredLanguage(), routingContext.request())
        if (Resources.isDefaultLocale("index_author", locale)) locale = Locale.getDefault()
        routingContext.data().put("lang", locale.toLanguageTag())
        routingContext.data().put("title", Resources.getValueOrResourcesString(if (isFramework) "index_title_framework" else "index_title_test", locale))
        routingContext.data().put("index_welcome", Resources.getValueOrResourcesString(if (isFramework) "index_welcome_framework" else "index_welcome_test", locale))
        routingContext.data().put("index_author", Resources.getValueOrResourcesString("index_author", locale))
        routingContext.data().put("index_item_1_title", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_1_title" else "index_test_item_1_title", locale))
        routingContext.data().put("index_item_2_title", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_2_title" else "index_test_item_2_title", locale))
        routingContext.data().put("index_item_3_title", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_3_title" else "index_test_item_3_title", locale))
        routingContext.data().put("index_item_1_text", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_1_text" else "index_test_item_1_text", locale))
        routingContext.data().put("index_item_2_text", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_2_text" else "index_test_item_2_text", locale))
        routingContext.data().put("index_item_3_text", Resources.getValueOrResourcesString(if (isFramework) "index_framework_item_3_text" else "index_test_item_3_text", locale))
        routingContext.data().put("index_forum", Resources.getValueOrResourcesString("index_forum", locale))
        routingContext.data().put("index_forum_product", Resources.getValueOrResourcesString(if (isFramework) "index_forum_product_framework" else "index_forum_product_test", locale))
        routingContext.data().put("index_item_1_icon", Resources.getValueOrResourcesString(if (isFramework) "index_item_1_icon_framework" else "index_item_1_icon_test", locale))
        routingContext.data().put("index_item_2_icon", Resources.getValueOrResourcesString(if (isFramework) "index_item_2_icon_framework" else "index_item_2_icon_test", locale))
        routingContext.data().put("index_item_3_icon", Resources.getValueOrResourcesString(if (isFramework) "index_item_3_icon_framework" else "index_item_3_icon_test", locale))
        routingContext.data().put("icon", Resources.getValueOrResourcesString(if (isFramework) "index_icon_framework" else "index_icon_test", locale))
        routingContext.data().put("footer", Resources.getValueOrResourcesString("index_footer", locale))
        // TODO: Give user APK directly
        routingContext.data().put("link", "https://github.com/MiPushFramework/${if (isFramework) "MiPushFramework" else "MiPushTester"}/releases")
        val engine: HandlebarsTemplateEngine = HandlebarsTemplateEngine.create(vertx)
        engine.render(routingContext.data(), "templates/index.hbs") {
            if (it.succeeded()) {
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Content-type", "text/html")
                        .end(it.result())
            } else {
                routingContext.fail(it.cause())
            }
        }
    }

    @Override
    override fun handlePush(routingContext: RoutingContext) {
        routingContext.request().bodyHandler body@{
            val request: PushRequest?
            try {
                request = ApiUtils.jsonToObject(it.toString(), PushRequest::class.java)
            } catch (e: IOException) {
                val response = routingContext.response()
                if (!response.ended() && !response.closed()) {
                    response.setStatusCode(400).end()
                }
                return@body
            }
            if (request == null) {
                val response = routingContext.response()
                if (!response.ended() && !response.closed()) {
                    response.setStatusCode(400).end()
                }
                return@body
            }
            if ((request.extras != null && (request.extras as MutableMap<String, String>).size > 10) ||
                    !DataVerifier.verify(request) ||
                    routingContext.request().getHeader(Constants.HEADER_PRODUCT) == null) {
                val response = routingContext.response()
                if (!response.ended() && !response.closed()) {
                    response.setStatusCode(400).end()
                }
                return@body
            }
            val message = Message()
            if (!request.passThrough || request.notifyForeground) {
                val title = Resources.getString("push_title", routingContext)
                val ticker = Resources.getString("push_ticker", routingContext)
                val description = Resources.getString("push_description", routingContext,
                        SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).time))
                message.ticker = ticker
                message.title = title
                message.description = description
            }
            // Payload is required for pass through messages
            message.payload = Date().toString()
            message.restrictedPackageName = routingContext.request().getHeader(Constants.HEADER_PRODUCT)
            message.passThrough = if (request.passThrough) Message.PASS_THROUGH_ENABLED else
                    Message.PASS_THROUGH_DISABLED
            message.notifyForeground = if (request.notifyForeground) Message.NOTIFY_FOREGROUND_ENABLE else
                    Message.NOTIFY_FOREGROUND_DISABLE
            message.connpt = if (request.enforceWifi) Message.CONNPT_WIFI else null
            when (request.display) {
                DISPLAY_LIGHTS -> message.notifyType = Message.NOTIFY_TYPE_DEFAULT_LIGHTS
                DISPLAY_SOUND -> message.notifyType = Message.NOTIFY_TYPE_DEFAULT_SOUND
                DISPLAY_VIBRATE -> message.notifyType = Message.NOTIFY_TYPE_DEFAULT_VIBRATE
                else -> message.notifyType = Message.NOTIFY_TYPE_DEFAULT_ALL
            }
            if (request.soundUri != null) message.soundUri = request.soundUri
            message.notifyId = request.notifyId
            val clickAction: String? = request.clickAction
            if (clickAction != null) {
                if (clickAction.startsWith("intent")) {
                    message.notifyEffect = Message.NOTIFY_NOTIFY_EFFECT_SPECIFIED_ACTIVITY
                    message.intentUrl = clickAction
                } else {
                    message.notifyEffect = Message.NOTIFY_NOTIFY_EFFECT_URL
                    message.webUri = clickAction
                }
            } else {
                message.notifyEffect = Message.NOTIFY_NOTIFY_EFFECT_LAUNCHER_APP
            }
            if (request.callback != null) message.callback = request.callback as String
            if (request.modelsExcept != null) message.modelNotIn = separateListToComma(request.modelsExcept as MutableList<String>)
            if (request.models != null) message.model = separateListToComma(request.models as MutableList<String>)
            if (request.locales != null) message.locale = separateListToComma(request.locales as MutableList<String>)
            if (request.localesExcept != null) message.localeNotIn = separateListToComma(request.localesExcept as List<String>)
            if (request.versions != null) message.appVersion = separateListToComma(request.versions as List<String>)
            if (request.versionsExcept != null) message.appVersionNotIn = separateListToComma(request.versionsExcept as List<String>)
            if (request.delayMs > 0) {
                val timeZone = TimeZone.getTimeZone("UTC")
                val calendar = Calendar.getInstance(timeZone)
                calendar.add(Calendar.MILLISECOND, request.delayMs)
                message.timeToSend = calendar.timeInMillis
            }
            val extras: MutableMap<String, String> = mutableMapOf()
            extras.put(Constants.EXTRA_CLIENT_VERSION, routingContext.request()?.getHeader(Constants.HEADER_VERSION) ?: "")
            extras.put(Constants.EXTRA_REQUEST_LOCALE, routingContext.request()?.getHeader(Constants.HEADER_LOCALE) ?: "")
            extras.put(Constants.EXTRA_REQUEST_TIME, System.currentTimeMillis().toString())
            getMiPushApi().pushOnce(message,
                    request.registrationId as String,
                    request.regIdType,
                    extras,
                    request.global,
                    Handler { ar ->
                        if (ar.succeeded()) {
                            val response = ar.result().body()
                            val httpResponse = routingContext.response()
                            if (!httpResponse.ended() && !httpResponse.closed()) {
                                httpResponse.setStatusCode(if (response.code == SendMessageResponse.CODE_SUCCESS)
                                                NO_CONTENT.code() else 500)
                                        .end()
                            }
                        } else {
                            logger.error("Cannot send message", ar.cause())
                            val response = routingContext.response()
                            if (!response.ended() && !response.closed()) {
                                response.setStatusCode(500)
                                        .end()
                            }
                        }
                    })
        }
    }

    open fun getMiPushApi(): MiPushApi {
        return MiPushApi(vertx!!.createHttpClient())
    }

    @Override
    override fun handleTesterIndex(routingContext: RoutingContext) {
        handleAnyIndex(false, routingContext)
    }

    @Override
    override fun handleUpdate(routingContext: RoutingContext) {
        val productId = routingContext.request().getHeader(Constants.HEADER_PRODUCT)
        if (productId == null) {
            val response = routingContext.response()
            if (!response.ended() && !response.closed()) {
                response.setStatusCode(NO_CONTENT.code()).end()
            }
            return
        }
        val repo: String
        val owner: String
        when (productId) {
            // Only "Authorized" offical clients can access this service.
            Constants.TESTER_CLIENT_ID -> {
                repo = "MiPushTester"
                owner = "MiPushFramework"
            }
            Constants.FRAMEWORK_CLIENT_ID -> {
                repo = "MiPushFramework"
                owner = "MiPushFramework"
            }
            else -> {
                logger.warn("An unknown client is attempting to get update status: $productId")
                val response = routingContext . response ()
                if (!response.ended() && !response.closed()) {
                    response.setStatusCode(NO_CONTENT.code()).end()
                }
                return
            }
        }
        getGitHubApi().getLatestRelease(owner, repo, Handler {
            if (it.succeeded()) {
                val release = it.result().body()
                if (release == null
                    || release.tagName.trim().equals("")
                    || release.tagName.trim().equals("")) {
                    val response = routingContext.response()
                    if (!response.ended() && !response.closed()) {
                        response.setStatusCode(NO_CONTENT.code()).end()
                    }
                } else {
                    val update = Update()
                    update.htmlLink = release.htmlUrl
                    try {
                        update.versionCode = Integer.parseInt(release.tagName)
                    } catch (ignored: NumberFormatException) {
                        update.versionCode = Integer.MAX_VALUE
                    }
                    update.versionName = release.name
                    val response = routingContext.response()
                    if (!response.ended() && !response.closed()) {
                        response.putHeader("Content-Type", "application/json")
                                .setChunked(true)
                                .setStatusCode(200)
                                .end(ApiUtils.tryObjectToJson(update))
                    }
                }
            } else {
                logger.error("Unable to get update", it.cause())
                val response = routingContext.response()
                if (!response.ended() && !response.closed()) {
                    response.setChunked(true)
                            .setStatusCode(500)
                            .end()
                }
            }
        })
    }

    open fun getGitHubApi(): GitHubApi {
        return GitHubApi(vertx!!.createHttpClient())
    }

    @Override
    override fun handleGetTopicList(routingContext: RoutingContext) {
        val response = routingContext.response()
        if (!response.ended() && !response.closed()) {
            response.setChunked(true)
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end(ApiUtils.tryObjectToJson(getTopicRegistry()
                            .allTopics()
                            .stream()
                            .peek{
                                it.title = Resources.getString(it.titleResource,
                                        routingContext)
                                it.description = Resources.getString(it.descriptionResource,
                                        routingContext)
                            }
                            .collect(Collectors.toList())
                    ))
        }
    }

    open fun getTopicRegistry(): TopicRegistry = TopicRegistry.get()
}

package moe.yuuta.server.topic.every5min

import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.HttpResponse
import moe.yuuta.common.Constants
import moe.yuuta.server.mipush.Message
import moe.yuuta.server.mipush.MiPushApi
import moe.yuuta.server.mipush.SendMessageResponse
import moe.yuuta.server.res.Resources
import moe.yuuta.server.topic.Topic
import moe.yuuta.server.topic.TopicExecuteVerticle
import java.util.*

// TODO: Add tests
class Every5MinTopicVerticle : TopicExecuteVerticle() {
    companion object {
        private const val FREQUENCY: Long = 5 * (1000 * 60)

        @JvmStatic
        fun getTopic(): Topic =
                Topic("topic_5min_title",
            "topic_5min_description",
            "5_min",
            Every5MinTopicVerticle())
    }
    private val logger = LoggerFactory.getLogger(Every5MinTopicVerticle::class.simpleName)

    private val timer = Timer()
    private val sendTask = object : TimerTask() {
        @Override
        override fun run() {
            Future.future<HttpResponse<SendMessageResponse>>{
                val message = Message()
                val title = Resources.getString("topic_5min_title", Locale.ENGLISH)
                val ticker = Resources.getString("push_ticker", Locale.ENGLISH)
                val description = Resources.getString("topic_5min_message", Locale.ENGLISH)
                message.ticker = ticker
                message.title = title
                message.description = (description)
                message.notifyId = (Date().toString().hashCode())
                val extras: MutableMap<String, String> = mutableMapOf()
                extras.put(Constants.EXTRA_REQUEST_TIME, System.currentTimeMillis().toString())
                MiPushApi(vertx.createHttpClient())
                        .pushOnceToTopic(message, topicId, extras, false, it)
            }.setHandler {
                if (!it.succeeded()) {
                    logger.error("Unable to send 5 min message", it.cause())
                } else {
                    logger.info("Successfully sent 5 min message")
                }
            }
        }
    }

    @Override
    override fun onRegister(registerFuture: Future<Void>) {
        timer.schedule(sendTask, FREQUENCY, FREQUENCY)
        registerFuture.complete()
    }

    @Override
    override fun onUnRegister(unRegisterFuture: Future<Void>) {
        timer.cancel()
        unRegisterFuture.complete()
    }
}

package moe.yuuta.server.topic

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

abstract class TopicExecuteVerticle : AbstractVerticle() {
    companion object {
        const val EXTRA_TOPIC_ID = "moe.yuuta.server.topic.TopicExecuteVerticle.EXTRA_TOPIC_ID"
    }

    protected lateinit var topicId: String

    @Throws(Exception::class)
    @Override
    final override fun start(startFuture: Future<Void>) {
        val id: String? = config().getString(EXTRA_TOPIC_ID, null)
        if (id == null) {
            startFuture.fail("Topic id is not provided")
            return
        }
        topicId = id
        onRegister(startFuture)
    }

    @Throws(Exception::class)
    @Override
    final override fun start() {
        super.start()
    }

    @Throws(Exception::class)
    final override fun stop() {
        super.stop()
    }

    @Throws(Exception::class)
    @Override
    final override fun stop(stopFuture: Future<Void>) {
        onUnRegister(stopFuture)
    }

    @Throws(Exception::class)
    open fun onRegister (registerFuture: Future<Void>) {
        registerFuture.complete()
    }

    @Throws(Exception::class)
    open fun onUnRegister (unRegisterFuture: Future<Void>) {
        unRegisterFuture.complete()
    }
}

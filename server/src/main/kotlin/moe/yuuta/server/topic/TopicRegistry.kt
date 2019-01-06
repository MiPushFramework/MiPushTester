package moe.yuuta.server.topic

import io.vertx.core.*
import moe.yuuta.server.topic.every5min.Every5MinTopicVerticle
import java.util.*
import java.util.stream.Collectors

open class TopicRegistry {
    companion object {
        private var instance: TopicRegistry? = null

        @JvmStatic
        fun get(): TopicRegistry {
            if (instance == null) instance = TopicRegistry()
            return instance as TopicRegistry
        }
    }

    private val mTopicRegistry: MutableMap<String, Topic> = mutableMapOf()

    open fun getDefaultTopics(): List<Topic> =
        Arrays.asList(Every5MinTopicVerticle.getTopic()).toList()

    fun init(vertx: Vertx, handler: Handler<AsyncResult<CompositeFuture>>) {
        CompositeFuture.all(
                getDefaultTopics()
                        .stream()
                        .map { topic -> Future.future<Any> { registerTopic(topic, vertx, it) } }
                        .collect(Collectors.toList())
        ).setHandler(handler)
    }

    open fun values(): Map<String, Topic> = mTopicRegistry.toMap()

    open fun allIds(): Set<String> = mTopicRegistry.keys

    open fun allTopics(): Collection<Topic> = mTopicRegistry.values

    fun registerTopic(topic: Topic, vertx: Vertx, handler: Handler<AsyncResult<Any>>) {
        topic.onRegister(vertx, Handler {
            if (it.succeeded()) {
                mTopicRegistry.put(topic.id, topic)
            }
            handler.handle(object : AsyncResult<Any> {
                @Override
                override fun result(): Any? = it.result()

                @Override
                override fun cause(): Throwable? = it.cause()

                @Override
                override fun succeeded(): Boolean = it.succeeded()

                @Override
                override fun failed(): Boolean = it.failed()
            })
        })
    }

    open fun getTopic(id: String): Topic? =
            mTopicRegistry.get(id)

    fun unregisterTopic(id: String, vertx: Vertx, handler: Handler<AsyncResult<Any>>) {
        val topic = getTopic(id)
        if (topic == null)
            throw IllegalArgumentException("$id can't be found")
        // TODO: Unregister when verticle "dies"
        topic.onUnRegister(vertx, Handler { it ->
            if (it.succeeded()) {
                mTopicRegistry.remove(id)
            }
            handler.handle(object : AsyncResult<Any> {
                @Override
                override fun result(): Any? = it.result()

                @Override
                override fun cause(): Throwable? = it.cause()

                @Override
                override fun succeeded(): Boolean = it.succeeded()

                @Override
                override fun failed(): Boolean = it.failed()
            })
        })
    }

    fun clear(vertx: Vertx, handler: Handler<AsyncResult<CompositeFuture>>) {
        val list = mutableListOf<Future<Any>>()
        val topics = mTopicRegistry.values.toMutableList()
        for (i in topics.indices) {
            val topic = topics.get(i)
            list.add(Future.future { unregisterTopic(topic.id, vertx, it.completer()) })
        }
        CompositeFuture.all(list).setHandler(handler)
    }
}

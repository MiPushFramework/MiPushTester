package moe.yuuta.server

import io.vertx.core.*
import moe.yuuta.server.api.ApiVerticle
import moe.yuuta.server.topic.TopicRegistry
import java.util.*
import java.util.function.Supplier

/**
 * Automated converted to Kotlin by Android Studio on Jan. 2 / 2019, not verified.
 */
class MainVerticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        val options = DeploymentOptions().setConfig(config())
        CompositeFuture.all(Arrays.asList(
                Future.future<CompositeFuture> { f -> TopicRegistry.get().init(vertx, f) },
                Future.future<String> { f -> vertx.deployVerticle(Supplier<Verticle> { ApiVerticle() }, options, f) }
        )).setHandler { ar ->
            if (ar.succeeded())
                startFuture.complete()
            else
                startFuture.fail(ar.cause())
        }
    }
}

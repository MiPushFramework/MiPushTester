package moe.yuuta.server.api

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.ext.web.Router

open class ApiVerticle : AbstractVerticle() {
    companion object {
        const val ROUTE = "/"
        const val ROUTE_TEST = ROUTE + "test"
        const val ROUTE_UPDATE = ROUTE + "update"
        const val ROUTE_TEST_TOPIC = "$ROUTE_TEST/topic"
    }

    @Override
    override fun start(startFuture: Future<Void>) {
        val router = Router.router(vertx)
        registerRoutes(router)
        val server = vertx.createHttpServer()
        server.requestHandler(router)
        server.listen(8080 /* port will be forwarded in Docker, so just hard code it here */
        ) {
            if (it.succeeded()) startFuture.complete()
            else startFuture.fail(it.cause())
        }
    }

    private fun registerRoutes(router: Router) {
        val handler = getApiHandler()
        router.route(POST, ROUTE_TEST).handler { handler.handlePush(it) }
        router.route(GET, ROUTE).handler { handler.handleFrameworkIndex(it) }
        router.route(GET, ROUTE_TEST).handler { handler.handleTesterIndex(it) }
        router.route(GET, ROUTE_UPDATE).handler { handler.handleUpdate(it) }
        router.route(GET, ROUTE_TEST_TOPIC).handler { handler.handleGetTopicList(it) }
    }

    open fun getApiHandler(): ApiHandler {
        return ApiHandler.apiHandler(vertx)
    }
}

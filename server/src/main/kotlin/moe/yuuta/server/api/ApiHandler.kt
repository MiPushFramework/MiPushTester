package moe.yuuta.server.api

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

interface ApiHandler {
    companion object {
        @JvmStatic
        fun apiHandler(vertx: Vertx?): ApiHandler {
            return ApiHandlerImpl(vertx)
        }
    }

    fun handlePush (routingContext: RoutingContext)
    fun handleFrameworkIndex(routingContext: RoutingContext)
    fun handleTesterIndex(routingContext: RoutingContext)
    fun handleUpdate(routingContext: RoutingContext)
    fun handleGetTopicList(routingContext: RoutingContext)
}

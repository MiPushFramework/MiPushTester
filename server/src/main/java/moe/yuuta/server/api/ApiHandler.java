package moe.yuuta.server.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface ApiHandler {
    static ApiHandler apiHandler(Vertx vertx) {
        return new ApiHandlerImpl(vertx);
    }

    void handlePush (RoutingContext routingContext);
    void handleIndex (RoutingContext routingContext);
}

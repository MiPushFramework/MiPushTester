package moe.yuuta.server.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import moe.yuuta.server.github.GitHubApi;
import moe.yuuta.server.mipush.MiPushApi;

public interface ApiHandler {
    static ApiHandler apiHandler(Vertx vertx) {
        return new ApiHandlerImpl(vertx);
    }

    void handlePush (RoutingContext routingContext);
    void handleFrameworkIndex(RoutingContext routingContext);
    void handleTesterIndex (RoutingContext routingContext);
    MiPushApi getMiPushApi ();
    void handleUpdate(RoutingContext routingContext);
    GitHubApi getGitHubApi ();
}

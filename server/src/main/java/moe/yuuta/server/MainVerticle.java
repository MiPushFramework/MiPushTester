package moe.yuuta.server;

import java.util.Arrays;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import moe.yuuta.server.api.ApiVerticle;
import moe.yuuta.server.topic.TopicRegistry;

@SuppressWarnings("unused")
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) {
        DeploymentOptions options = new DeploymentOptions().setConfig(config());
        CompositeFuture.all(Arrays.asList(
                Future.<CompositeFuture>future(f -> TopicRegistry.getInstance().init(vertx, f)),
                Future.<String>future(f -> vertx.deployVerticle(ApiVerticle::new, options, f))
        )).setHandler(ar -> {
            if (ar.succeeded()) startFuture.complete();
            else startFuture.fail(ar.cause());
        });
    }
}

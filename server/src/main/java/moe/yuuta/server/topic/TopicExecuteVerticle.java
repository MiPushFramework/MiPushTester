package moe.yuuta.server.topic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class TopicExecuteVerticle extends AbstractVerticle {
    static final String EXTRA_TOPIC_ID = TopicExecuteVerticle.class.getName() + ".EXTRA_TOPIC_ID";

    protected String topicId;

    @Override
    public final void start(Future<Void> startFuture) throws Exception {
        topicId = config().getString(EXTRA_TOPIC_ID, null);
        if (topicId == null) {
            startFuture.fail("Topic id is not provided");
            return;
        }
        onRegister(startFuture);
    }

    @Override
    public final void start() throws Exception {
        super.start();
    }

    @Override
    public final void stop() throws Exception {
        super.stop();
    }

    @Override
    public final void stop(Future<Void> stopFuture) throws Exception {
        onUnRegister(stopFuture);
    }

    public void onRegister (Future<Void> registerFuture) throws Exception {
        registerFuture.complete();
    }

    public void onUnRegister (Future<Void> unRegisterFuture) throws Exception {
        unRegisterFuture.complete();
    }
}

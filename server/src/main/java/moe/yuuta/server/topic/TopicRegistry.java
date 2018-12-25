package moe.yuuta.server.topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import moe.yuuta.server.topic.every5min.Every5MinTopicVerticle;

public class TopicRegistry {
    private static TopicRegistry instance;

    public static TopicRegistry getInstance() {
        if (instance == null) instance = new TopicRegistry();
        return instance;
    }

    private Map<String, Topic> mTopicRegistry = new HashMap<>(0);

    TopicRegistry () {
    }

    List<Topic> getDefaultTopics () {
        return Arrays.asList(Every5MinTopicVerticle.getTopic());
    }

    public void init (Vertx vertx, Handler<AsyncResult<CompositeFuture>> handler) {
        CompositeFuture.all(
                getDefaultTopics()
                        .stream()
                        .map((Function<Topic, Future>) topic -> Future.future(f -> registerTopic(topic, vertx, f)))
                        .collect(Collectors.toList())
        ).setHandler(handler);
    }

    public Map<String, Topic> values () {
        return new HashMap<>(mTopicRegistry);
    }

    public Set<String> allIds () {
        return mTopicRegistry.keySet();
    }

    public Collection<Topic> allTopics () {
        return mTopicRegistry.values();
    }

    public void registerTopic (Topic topic, Vertx vertx, Handler<AsyncResult<Object>> handler) {
        topic.onRegister(vertx, ar -> {
            if (ar.succeeded()) {
                mTopicRegistry.put(topic.getId(), topic);
            }
            handler.handle(new AsyncResult<Object>() {
                @Override
                public Object result() {
                    return ar.result();
                }

                @Override
                public Throwable cause() {
                    return ar.cause();
                }

                @Override
                public boolean succeeded() {
                    return ar.succeeded();
                }

                @Override
                public boolean failed() {
                    return ar.failed();
                }
            });
        });
    }

    public Topic getTopic (String id) {
        return mTopicRegistry.get(id);
    }

    public void unregisterTopic (String id, Vertx vertx, Handler<AsyncResult<Object>> handler) {
        Topic topic = getTopic(id);
        if (topic == null)
            throw new IllegalArgumentException(id + " can't be found");
        // TODO: Unregister when verticle "dies"
        topic.onUnRegister(vertx, ar -> {
            if (ar.succeeded()) {
                mTopicRegistry.remove(id);
            }
            handler.handle(new AsyncResult<Object>() {
                @Override
                public Object result() {
                    return ar.result();
                }

                @Override
                public Throwable cause() {
                    return ar.cause();
                }

                @Override
                public boolean succeeded() {
                    return ar.succeeded();
                }

                @Override
                public boolean failed() {
                    return ar.failed();
                }
            });
        });
    }

    public void clear (Vertx vertx, Handler<AsyncResult<CompositeFuture>> handler) {
        List<Future> list = new ArrayList<>(mTopicRegistry.size());
        List<Topic> topics = new ArrayList<>(mTopicRegistry.values());
        for (int i = 0; i < topics.size(); i ++) {
            Topic topic = topics.get(i);
            list.add(Future.future(f -> unregisterTopic(topic.getId(), vertx, f.completer())));
        }
        CompositeFuture.all(list).setHandler(handler);
    }
}

package moe.yuuta.server.topic.every5min;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import moe.yuuta.common.Constants;
import moe.yuuta.server.mipush.Message;
import moe.yuuta.server.mipush.MiPushApi;
import moe.yuuta.server.mipush.SendMessageResponse;
import moe.yuuta.server.res.Resources;
import moe.yuuta.server.topic.Topic;
import moe.yuuta.server.topic.TopicExecuteVerticle;

// TODO: Add tests
public class Every5MinTopicVerticle extends TopicExecuteVerticle {
    public static Topic getTopic () {
        return new Topic("topic_5min_title",
                            "topic_5min_description",
                "5_min",
                new Every5MinTopicVerticle());
    }

    private static final int FREQUENCY = 5 * (1000 * 60);
    private final Logger logger = LoggerFactory.getLogger(Every5MinTopicVerticle.class.getSimpleName());

    private Timer timer = new Timer();
    private TimerTask sendTask = new TimerTask() {
        @Override
        public void run() {
            Future.<HttpResponse<SendMessageResponse>>future(f -> {
                Message message = new Message();
                String title = Resources.getString("topic_5min_title", Locale.ENGLISH);
                String ticker = Resources.getString("push_ticker", Locale.ENGLISH);
                String description = Resources.getString("topic_5min_message", Locale.ENGLISH);
                message.setTicker(ticker);
                message.setTitle(title);
                message.setDescription(description);
                message.setNotifyId(new Date().toString().hashCode());
                Map<String, String> extras = new HashMap<>(10);
                extras.put(Constants.EXTRA_REQUEST_TIME, Long.toString(System.currentTimeMillis()));
                new MiPushApi(vertx.createHttpClient())
                        .pushOnceToTopic(message, topicId, extras, false, f);
            }).setHandler(ar -> {
                if (!ar.succeeded()) {
                    logger.error("Unable to send 5 min message", ar.cause());
                } else {
                    logger.info("Successfully sent 5 min message");
                }
            });
        }
    };

    @Override
    public void onRegister(Future<Void> registerFuture) {
        timer.schedule(sendTask, FREQUENCY, FREQUENCY);
        registerFuture.complete();
    }

    @Override
    public void onUnRegister(Future<Void> unRegisterFuture) {
        if (timer != null) {
            timer.cancel();
        }
        unRegisterFuture.complete();
    }
}

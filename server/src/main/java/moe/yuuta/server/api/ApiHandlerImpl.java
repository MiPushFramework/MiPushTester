package moe.yuuta.server.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import moe.yuuta.common.Constants;
import moe.yuuta.server.dataverify.DataVerifier;
import moe.yuuta.server.mipush.Message;
import moe.yuuta.server.mipush.MiPushApi;
import moe.yuuta.server.mipush.SendMessageResponse;
import moe.yuuta.server.res.Resources;

import static moe.yuuta.common.Constants.DISPLAY_ALL;
import static moe.yuuta.common.Constants.DISPLAY_LIGHTS;
import static moe.yuuta.common.Constants.DISPLAY_SOUND;
import static moe.yuuta.common.Constants.DISPLAY_VIBRATE;
import static moe.yuuta.server.api.ApiUtils.separateListToComma;

public class ApiHandlerImpl implements ApiHandler {
    private final Logger logger = LoggerFactory.getLogger(ApiHandlerImpl.class.getSimpleName());

    static final String HTML_FRAMEWORK_INDEX = "<html>" +
            "<head>" +
            "<title>MiPushFramework</title>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body>" +
            "<p>Homepage is still under construction, check it back later.</p>" +
            "<a href=\"https://github.com/Trumeet/MiPushFramework\">GitHub</a>" +
            "</body>" +
            "</html>";

    static final String HTML_TESTER_INDEX = "<html>" +
            "<head>" +
            "<title>MiPush Tester</title>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body>" +
            "<p>Homepage is still under construction, check it back later.</p>" +
            "<a href=\"https://github.com/Trumeet/MiPushTester\">GitHub</a>" +
            "</body>" +
            "</html>";

    private Vertx vertx;

    ApiHandlerImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handleFrameworkIndex(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Content-Type", "text/html")
                .setStatusCode(200)
                .end(HTML_FRAMEWORK_INDEX);
    }

    @Override
    public void handlePush(RoutingContext routingContext) {
        routingContext.request().bodyHandler(buffer -> {
            PushRequest request;
            try {
                request = ApiUtils.jsonToObject(buffer.toString(), PushRequest.class);
            } catch (IOException e) {
                routingContext.response().setStatusCode(400).end();
                return;
            }
            if ((request.getExtras() != null && request.getExtras().size() > 10) ||
                    !DataVerifier.verify(request)) {
                routingContext.response().setStatusCode(400).end();
                return;
            }
            String title = Resources.getString("push_title", routingContext);
            String ticker = Resources.getString("push_ticker", routingContext);
            String description = Resources.getString("push_description", routingContext,
                    new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).getTime()));
            Message message = new Message();
            message.setTicker(ticker);
            message.setRestrictedPackageName(Constants.CLIENT_ID);
            // FIXME
            message.setPassThrough(request.isPassThrough() ? Message.PASS_THROUGH_ENABLED :
                    Message.PASS_THROUGH_DISABLED);
            message.setTitle(title);
            message.setDescription(description);
            message.setNotifyForeground(request.isNotifyForeground() ? Message.NOTIFY_FOREGROUND_ENABLE :
                    Message.NOTIFY_FOREGROUND_DISABLE);
            message.setConnpt(request.isEnforceWifi() ? Message.CONNPT_WIFI : null);
            switch (request.getDisplay()) {
                case DISPLAY_LIGHTS:
                    message.setNotifyType(Message.NOTIFY_TYPE_DEFAULT_LIGHTS);
                    break;
                case DISPLAY_SOUND:
                    message.setNotifyType(Message.NOTIFY_TYPE_DEFAULT_SOUND);
                    break;
                case DISPLAY_VIBRATE:
                    message.setNotifyType(Message.NOTIFY_TYPE_DEFAULT_VIBRATE);
                    break;
                case DISPLAY_ALL:
                default:
                    message.setNotifyType(Message.NOTIFY_TYPE_DEFAULT_ALL);
                    break;
            }
            if (request.getSoundUri() != null) message.setSoundUri(request.getSoundUri());
            message.setNotifyId(request.getNotifyId());
            if (request.getClickAction() != null) {
                if (request.getClickAction().startsWith("intent")) {
                    message.setNotifyEffect(Message.NOTIFY_NOTIFY_EFFECT_SPECIFIED_ACTIVITY);
                    message.setIntentUri(request.getClickAction());
                } else {
                    message.setNotifyEffect(Message.NOTIFY_NOTIFY_EFFECT_URL);
                    message.setWebUri(request.getClickAction());
                }
            } else {
                message.setNotifyEffect(Message.NOTIFY_NOTIFY_EFFECT_LAUNCHER_APP);
            }
            if (request.getCallback() != null) message.setCallback(request.getCallback());
            if (request.getModelsExcept() != null) message.setModelNotIn(separateListToComma(request.getModelsExcept()));
            if (request.getModels() != null) message.setModel(separateListToComma(request.getModels()));
            if (request.getLocales() != null) message.setLocale(separateListToComma(request.getLocales()));
            if (request.getLocalesExcept() != null) message.setLocaleNotIn(separateListToComma(request.getLocalesExcept()));
            if (request.getVersions() != null) message.setAppVersion(separateListToComma(request.getVersions()));
            if (request.getVersionsExcept() != null) message.setAppVersionNotIn(separateListToComma(request.getVersionsExcept()));
            if (request.getDelayMs() > 0) {
                TimeZone timeZone = TimeZone.getTimeZone("UTC");
                Calendar calendar = Calendar.getInstance(timeZone);
                calendar.add(Calendar.MILLISECOND, request.getDelayMs());
                message.setTimeToSend(calendar.getTimeInMillis());
            }
            Map<String, String> extras = new HashMap<>(10);
            extras.put(Constants.EXTRA_CLIENT_VERSION, routingContext.request().getHeader(Constants.HEADER_VERSION));
            extras.put(Constants.EXTRA_REQUEST_LOCALE, routingContext.request().getHeader(Constants.HEADER_LOCALE));
            extras.put(Constants.EXTRA_REQUEST_TIME, Long.toString(System.currentTimeMillis()));
            getMiPushApi().pushOnceToId(message,
                    new String[]{ request.getRegistrationId() },
                    extras,
                    ar -> {
                if (ar.succeeded()) {
                    SendMessageResponse response = ar.result().body();
                    routingContext.response()
                            .setStatusCode(response.getCode() == SendMessageResponse.CODE_SUCCESS ?
                                    HttpResponseStatus.NO_CONTENT.code() : 500)
                            .end();
                } else {
                    logger.error("Cannot send message", ar.cause());
                    routingContext.response()
                            .setStatusCode(500)
                            .end();
                }
            });
        });
    }

    @Override
    public MiPushApi getMiPushApi () {
        return new MiPushApi(vertx.createHttpClient());
    }

    @Override
    public void handleTesterIndex(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Content-Type", "text/html")
                .setStatusCode(200)
                .end(HTML_TESTER_INDEX);
    }
}

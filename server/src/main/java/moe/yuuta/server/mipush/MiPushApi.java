package moe.yuuta.server.mipush;

import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import moe.yuuta.common.Constants;
import moe.yuuta.server.formprocessor.HttpForm;

// TODO: Add tests
public class MiPushApi {
    private static final String HOST_CHINA = "api.xmpush.xiaomi.com";
    private static final String HOST_GLOBAL = "api.xmpush.global.xiaomi.com";

    private HttpClient httpClient;

    public MiPushApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private static String buildExtras (Map<String, String> customExtras) {
        StringBuilder extrasBuilder = new StringBuilder();
        for (String key : customExtras.keySet()) {
            extrasBuilder.append("extra.");
            extrasBuilder.append(key);
            extrasBuilder.append("=");
            extrasBuilder.append(customExtras.get(key));
            extrasBuilder.append("&");
        }
        String extras = extrasBuilder.toString();
        extras = extras.substring(0, extras.length() - 1);
        return extras;
    }

    public void pushOnce(Message message, String regId, int regIdType, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
        String apiUrl = "/v3/message/regid";
        switch (regIdType) {
            case Constants.REG_ID_TYPE_REG_ID:
                message.setRegId(regId);
                apiUrl = "/v3/message/regid";
                break;
            case Constants.REG_ID_TYPE_ACCOUNT:
                message.setAccount(regId);
                apiUrl = "/v2/message/user_account";
                break;
            case Constants.REG_ID_TYPE_ALIAS:
                message.setAlias(regId);
                apiUrl = "/v3/message/alias";
                break;
        }
        Buffer arguments = HttpForm.toBuffer(message);
        if (customExtras != null) {
            arguments.appendString("&" + buildExtras(customExtras));
        }
        LoggerFactory.getLogger(MiPushApi.class).error("Sending to " + apiUrl + ", regIdWithType=" + regId + "," + regIdType);
        LoggerFactory.getLogger(MiPushApi.class).error(arguments.toString());
        generateHttpCall(HttpMethod.POST, apiUrl, useGlobal)
                .as(BodyCodec.json(SendMessageResponse.class))
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .sendBuffer(arguments, handler);
    }

    public void pushOnceToTopic (Message message, String topic, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
        Buffer arguments = HttpForm.toBuffer(message);
        arguments.appendString("&topic=" + topic);
        if (customExtras != null) {
            arguments.appendString("&" + buildExtras(customExtras));
        }
        generateHttpCall(HttpMethod.POST, "/v3/message/topic", useGlobal)
                .as(BodyCodec.json(SendMessageResponse.class))
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .sendBuffer(arguments, handler);
    }

    private HttpRequest<Buffer> generateHttpCall (HttpMethod method, String path, boolean useGlobal) {
        WebClient webClient = WebClient.wrap(httpClient);
        return webClient.request(method, new RequestOptions()
                                        .setPort(443)
                                        .setHost(useGlobal ? HOST_GLOBAL : HOST_CHINA)
                                        .setSsl(true)
                                        .setURI(path))
                .putHeader("Authorization", "key=" + System.getenv("MIPUSH_AUTH"));
    }
}

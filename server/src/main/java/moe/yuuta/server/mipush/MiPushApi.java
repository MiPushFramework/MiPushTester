package moe.yuuta.server.mipush;

import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
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

    public void pushOnceToId (Message message, String[] regIds, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
        Buffer arguments = HttpForm.toBuffer(message);
        StringBuilder regIdArgumentBuilder = new StringBuilder();
        for (int i = 0; i < regIds.length; i ++) {
            String regId = regIds[i];
            regIdArgumentBuilder.append(regId);
            if (i != regIds.length - 1)
                regIdArgumentBuilder.append(",");
        }
        arguments.appendString("&registration_id=" + regIdArgumentBuilder.toString());
        if (customExtras != null) {
            arguments.appendString("&" + buildExtras(customExtras));
        }
        generateHttpCall(HttpMethod.POST, "/v3/message/regid", useGlobal)
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

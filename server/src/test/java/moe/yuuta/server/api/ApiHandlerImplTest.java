package moe.yuuta.server.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import moe.yuuta.common.Constants;
import moe.yuuta.server.mipush.Message;
import moe.yuuta.server.mipush.MiPushApi;
import moe.yuuta.server.mipush.SendMessageResponse;
import moe.yuuta.server.res.Resources;

@RunWith(VertxUnitRunner.class)
public class ApiHandlerImplTest {
    private interface SendPushCallback {
        void pushOnceToId(Message message, String[] regIds, Map<String, String> customExtras, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler);
    }

    private Vertx vertx;
    private ApiHandler apiHandler;
    private ApiVerticle apiVerticle; // We won't build a mocked RoutingContext
    private volatile SendPushCallback callback;

    @Before
    public void setUp (TestContext testContext) {
        vertx = Vertx.vertx();
        apiHandler = Mockito.spy(ApiHandler.apiHandler(vertx));
        Mockito.when(apiHandler.getMiPushApi()).thenReturn(new MiPushApi(null) {
            @Override
            public void pushOnceToId(Message message, String[] regIds, Map<String, String> customExtras, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
                if (callback == null) {
                    super.pushOnceToId(message, regIds, customExtras, handler);
                } else {
                    callback.pushOnceToId(message, regIds, customExtras, handler);
                }
            }
        });
        apiVerticle = Mockito.spy(new ApiVerticle());
        Mockito.when(apiVerticle.getApiHandler()).thenReturn(apiHandler);
        vertx.deployVerticle(apiVerticle, testContext.asyncAssertSuccess());
    }

    @Test(timeout = 2000)
    public void handleFrameworkIndex(TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE, httpClientResponse -> {
            testContext.assertEquals(200, httpClientResponse.statusCode());
            testContext.assertEquals("text/html".trim().toLowerCase(), httpClientResponse.getHeader("Content-Type").trim().toLowerCase());
            httpClientResponse.bodyHandler(buffer -> {
                testContext.assertEquals(ApiHandlerImpl.HTML_FRAMEWORK_INDEX.trim(), buffer.toString().trim());
                async.complete();
            });
        });
    }

    // TODO: Add invalid request check
    // TODO: Add error response check
    @Test(timeout = 2000)
    public void shouldHandleSuccessfulPush(TestContext testContext) {
        Async async = testContext.async(2);
        final String regId = "123";
        final String soundUri = "qjiwor";
        final int notifyId = 123456;
        final String clickAction = "intent:xx";
        final String callBack = "https://call.back";
        final List<String> modelsIn = Arrays.asList("Pixel", "OnePlus");
        final List<String> modelsOut = Arrays.asList("Emulator", "Laptop");
        final List<String> versionsIn = Arrays.asList("1.0", "2.0");
        final List<String> versionsOut = Arrays.asList("0.1", "0.2");
        final List<String> localesIn = Arrays.asList("zh_CN", "zh_TW");
        final List<String> localesOut = Arrays.asList("zh_HK", "en_UK");
        final int delayMs = 100;
        final String requestVersion = "Haoye1.0";
        final String requestLocale = "zh_TW";

        // TODO: Test multiple situations for variety arguments (e.g. display)
        this.callback = (Message message, String[] regIds, Map<String, String> customExtras, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) -> {
                testContext.assertNotNull(message);
                testContext.assertEquals(message.getTitle(), Resources.getString("push_title", Locale.ENGLISH));
                testContext.assertEquals(message.getTicker(), Resources.getString("push_ticker", Locale.ENGLISH));
                // TODO: Fully match the description
                testContext.assertNotNull(message.getDescription());
                testContext.assertNotEquals(message.getDescription(), "");
                testContext.assertEquals(message.getRestrictedPackageName(), Constants.CLIENT_ID);
                testContext.assertEquals(message.getPassThrough(), Message.PASS_THROUGH_ENABLED);
                testContext.assertEquals(message.getNotifyForeground(), Message.NOTIFY_FOREGROUND_DISABLE);
                testContext.assertEquals(message.getConnpt(), Message.CONNPT_WIFI);
                testContext.assertEquals(message.getNotifyType(), Message.NOTIFY_TYPE_DEFAULT_VIBRATE);
                testContext.assertEquals(message.getSoundUri(), soundUri);
                testContext.assertNull(message.getWebUri());
                testContext.assertEquals(message.getIntentUri(), clickAction);
                testContext.assertEquals(message.getNotifyEffect(), Message.NOTIFY_NOTIFY_EFFECT_SPECIFIED_ACTIVITY);
                testContext.assertEquals(message.getCallback(), callBack);
                testContext.assertEquals(message.getLocale(), ApiUtils.separateListToComma(localesIn));
                testContext.assertEquals(message.getLocaleNotIn(), ApiUtils.separateListToComma(localesOut));
                testContext.assertEquals(message.getAppVersion(), ApiUtils.separateListToComma(versionsIn));
                testContext.assertEquals(message.getAppVersionNotIn(), ApiUtils.separateListToComma(versionsOut));
                testContext.assertEquals(message.getModel(), ApiUtils.separateListToComma(modelsIn));
                testContext.assertEquals(message.getModelNotIn(), ApiUtils.separateListToComma(modelsOut));
                // TODO: Add time check
                // testContext.assertEquals(message.getTimeToSend(), delayMs);
                testContext.assertNotEquals(message.getTimeToSend(), 0);
                async.complete();
            };
        // TODO: Add extras test
        PushRequest request = new PushRequest();
        request.setRegistrationId(regId);
        request.setPassThrough(true);
        request.setNotifyForeground(false);
        request.setEnforceWifi(true);
        request.setDisplay(Constants.DISPLAY_VIBRATE);
        request.setSoundUri(soundUri);
        request.setNotifyId(notifyId);
        request.setClickAction(clickAction);
        request.setCallback(callBack);
        request.setLocales(localesIn);
        request.setLocalesExcept(localesOut);
        request.setModels(modelsIn);
        request.setModelsExcept(modelsOut);
        request.setVersions(versionsIn);
        request.setVersionsExcept(versionsOut);
        request.setDelayMs(delayMs);
        vertx.createHttpClient().post(8080, "localhost", ApiVerticle.ROUTE_TEST, httpClientResponse -> {
            async.countDown();
        })
                .putHeader("Content-Type", "application/json")
                .putHeader("Accept-Language", Locale.ENGLISH.toString())
                .putHeader(Constants.HEADER_LOCALE, requestLocale)
                .putHeader(Constants.HEADER_VERSION, requestVersion)
                .setChunked(true)
                .write(ApiUtils.tryObjectToJson(request))
                .end();
    }

    @Test(timeout = 2000)
    public void handleTesterIndex(TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE_TEST, httpClientResponse -> {
            testContext.assertEquals(200, httpClientResponse.statusCode());
            testContext.assertEquals("text/html".trim().toLowerCase(), httpClientResponse.getHeader("Content-Type").trim().toLowerCase());
            httpClientResponse.bodyHandler(buffer -> {
                testContext.assertEquals(ApiHandlerImpl.HTML_TESTER_INDEX.trim(), buffer.toString().trim());
                async.complete();
            });
        });
    }

    @After
    public void tearDown (TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }
}
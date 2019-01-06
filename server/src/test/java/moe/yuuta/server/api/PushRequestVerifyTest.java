package moe.yuuta.server.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import moe.yuuta.common.Constants;
import moe.yuuta.server.mipush.Message;
import moe.yuuta.server.mipush.MiPushApi;
import moe.yuuta.server.mipush.SendMessageResponse;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

@RunWith(VertxUnitRunner.class)
public class PushRequestVerifyTest {
    private Vertx vertx;
    private ApiHandlerImpl apiHandler;
    private ApiVerticle apiVerticle; // We won't build a mocked RoutingContext
    private PushRequest normalRequest;
    private volatile boolean nextApiCallShouldOK;

    @Before
    public void setUp (TestContext testContext) {
        vertx = Vertx.vertx();
        apiHandler = Mockito.spy(new ApiHandlerImpl(vertx));
        Mockito.when(apiHandler.getMiPushApi()).thenReturn(new MiPushApi(null) {
            @Override
            public void pushOnce(Message message, String regIds, int regIdType, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
                if (!nextApiCallShouldOK)
                    testContext.fail("Unaccepted call");
                else
                    handler.handle(new AsyncResult<HttpResponse<SendMessageResponse>>() {
                        @Override
                        public HttpResponse<SendMessageResponse> result() {
                            return null;
                        }

                        @Override
                        public Throwable cause() {
                            return null;
                        }

                        @Override
                        public boolean succeeded() {
                            return false;
                        }

                        @Override
                        public boolean failed() {
                            return false;
                        }
                    });
            }
        });
        apiVerticle = Mockito.spy(new ApiVerticle());
        Mockito.when(apiVerticle.getApiHandler()).thenReturn(apiHandler);
        vertx.deployVerticle(apiVerticle, testContext.asyncAssertSuccess());
    }

    private void restoreRequest () {
        normalRequest = new PushRequest();
        normalRequest.setDelayMs(0);
        normalRequest.setRegistrationId("abc");
        normalRequest.setDisplay(Constants.DISPLAY_ALL);
        normalRequest.setExtras(null);
    }

    @Test
    public void shouldPassCorrectPushRequest (TestContext testContext) {
        Async async = testContext.async();
        // Should pass
        nextApiCallShouldOK = true;
        restoreRequest();
        vertx.createHttpClient().post(8080, "localhost", ApiVerticle.ROUTE_TEST, httpClientResponse -> {
            testContext.assertEquals(INTERNAL_SERVER_ERROR.code(), httpClientResponse.statusCode());
            nextApiCallShouldOK = false;
            async.countDown();
        }).putHeader("Content-Type", "application/json")
                .putHeader("Accept-Language", Locale.ENGLISH.toString())
                .putHeader(Constants.HEADER_PRODUCT, "123")
                .setChunked(true)
                .end(ApiUtils.tryObjectToJson(normalRequest));
    }

    @Test
    public void shouldRefuseBadPushRequest (TestContext testContext) throws InterruptedException {
        Async async = testContext.async(7);
        // Missing request test
        send(testContext, async, false, true /* Keep the variable amount always 1 */, false);

        // Missing product test
        restoreRequest();
        send(testContext, async, true, false, false);

        // Incorrect delay test
        restoreRequest();
        normalRequest.setDelayMs(-100);
        send(testContext, async, true, true, false);
        restoreRequest();
        normalRequest.setDelayMs(Constants.PUSH_DELAY_MS_MAX + 100);
        send(testContext, async, true, true, false);

        // Incorrect delay test
        restoreRequest();
        normalRequest.setDisplay(-100);
        send(testContext, async, true, true, false);

        // Null registration ID test
        restoreRequest();
        normalRequest.setRegistrationId(null);
        send(testContext, async, true, true, false);

        // Invalid registration ID type test
        restoreRequest();
        normalRequest.setRegIdType(233333);
        send(testContext, async, true, true, false);

        // Too many extras test
        Map<String, String> strings = new HashMap<>(20);
        for (int i = 0; i < 20; i ++)
            strings.put("K" + i, "V" + i);
        restoreRequest();
        normalRequest.setExtras(strings);
        send(testContext, async, true, true, true);
    }

    private void send (TestContext testContext, Async async, boolean addProduct, boolean addRequest, boolean complete) throws InterruptedException {
        // TODO: Sometimes return 503 if two tests are called without any delays?
        Thread.sleep(1000);
        HttpClientRequest request = vertx.createHttpClient().post(8080, "localhost", ApiVerticle.ROUTE_TEST, httpClientResponse -> {
            testContext.assertEquals(BAD_REQUEST.code(), httpClientResponse.statusCode());
            async.countDown();
            if (complete) async.complete();
        }).putHeader("Content-Type", "application/json")
                .putHeader("Accept-Language", Locale.ENGLISH.toString())
                .setChunked(true);
        if (addProduct) {
            request.putHeader(Constants.HEADER_PRODUCT, "123");
        }
        if (addRequest) request.end(ApiUtils.tryObjectToJson(normalRequest));
        else request.end();
    }

    @After
    public void tearDown (TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }
}

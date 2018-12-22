package moe.yuuta.server.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import moe.yuuta.common.Constants;
import moe.yuuta.server.api.update.Update;
import moe.yuuta.server.github.GitHubApi;
import moe.yuuta.server.github.Release;
import moe.yuuta.server.mipush.Message;
import moe.yuuta.server.mipush.MiPushApi;
import moe.yuuta.server.mipush.SendMessageResponse;
import moe.yuuta.server.res.Resources;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

@RunWith(VertxUnitRunner.class)
public class ApiHandlerImplTest {
    private interface SendPushCallback {
        void pushOnceToId(Message message, String[] regIds, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler);
    }

    private interface GetLatestReleaseCallback {
        void getLatestRelease (String owner, String repo, Handler<AsyncResult<HttpResponse<Release>>> handler);
    }

    private Vertx vertx;
    private ApiHandler apiHandler;
    private ApiVerticle apiVerticle; // We won't build a mocked RoutingContext
    private volatile SendPushCallback sendPushCallback;
    private volatile GetLatestReleaseCallback getLatestReleaseCallback;

    @Before
    public void setUp (TestContext testContext) {
        vertx = Vertx.vertx();
        apiHandler = Mockito.spy(ApiHandler.apiHandler(vertx));
        Mockito.when(apiHandler.getMiPushApi()).thenReturn(new MiPushApi(null) {
            @Override
            public void pushOnceToId(Message message, String[] regIds, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) {
                if (sendPushCallback == null) {
                    super.pushOnceToId(message, regIds, customExtras, useGlobal, handler);
                } else {
                    sendPushCallback.pushOnceToId(message, regIds, customExtras, useGlobal, handler);
                }
            }
        });
        Mockito.when(apiHandler.getGitHubApi()).thenReturn(new GitHubApi(null) {
            @Override
            public void getLatestRelease(String owner, String repo, Handler<AsyncResult<HttpResponse<Release>>> handler) {
                if (getLatestReleaseCallback == null) {
                    super.getLatestRelease(owner, repo, handler);
                } else {
                    getLatestReleaseCallback.getLatestRelease(owner, repo, handler);
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

    @Test(timeout = 2000)
    public void shouldGetExistingUpdate (TestContext testContext) {
        Async async = testContext.async();
        this.getLatestReleaseCallback = ((owner, repo, handler) -> {
            handler.handle(new AsyncResult<HttpResponse<Release>>() {
                @Override
                public HttpResponse<Release> result() {
                    return new HttpResponse<Release>() {
                        @Override
                        public HttpVersion version() {
                            return null;
                        }

                        @Override
                        public int statusCode() {
                            return 200;
                        }

                        @Override
                        public String statusMessage() {
                            return "OK";
                        }

                        @Override
                        public MultiMap headers() {
                            return null;
                        }

                        @Override
                        public String getHeader(String s) {
                            return null;
                        }

                        @Override
                        public MultiMap trailers() {
                            return null;
                        }

                        @Override
                        public String getTrailer(String s) {
                            return null;
                        }

                        @Override
                        public List<String> cookies() {
                            return null;
                        }

                        @Override
                        public Release body() {
                            Release release = new Release();
                            release.setBody("Body");
                            release.setHtmlUrl("https://google.com");
                            release.setId(100);
                            release.setName("1.0.0");
                            release.setTagName("100");
                            release.setUrl("https://api.github.com");
                            return release;
                        }

                        @Override
                        public Buffer bodyAsBuffer() {
                            return Buffer.buffer(ApiUtils.tryObjectToJson(body()));
                        }

                        @Override
                        public JsonArray bodyAsJsonArray() {
                            return null;
                        }
                    };
                }

                @Override
                public Throwable cause() {
                    return null;
                }

                @Override
                public boolean succeeded() {
                    return true;
                }

                @Override
                public boolean failed() {
                    return false;
                }
            });
        });

        vertx.createHttpClient().get(8080, "localhost", ApiVerticle.ROUTE_UPDATE,
                httpClientResponse -> {
                    testContext.assertEquals(200, httpClientResponse.statusCode());
                    httpClientResponse.bodyHandler(buffer -> {
                        Update update;
                        try {
                            update = ApiUtils.jsonToObject(buffer.toString(), Update.class);
                        } catch (IOException e) {
                            testContext.fail(e);
                            return; // Should never happen
                        }
                        testContext.assertNotNull(update);
                        testContext.assertEquals(update.getHtmlLink(), "https://google.com");
                        testContext.assertEquals(update.getVersionCode(), 100);
                        testContext.assertEquals(update.getVersionName(), "1.0.0");
                        async.complete();
                    });
                })
                .putHeader(Constants.HEADER_PRODUCT, Constants.TESTER_CLIENT_ID)
                .end();
    }

    @Test(timeout = 2000)
    public void shouldApplyUpdateRepoMapping (TestContext testContext) throws Exception {
        Async async = testContext.async(3);
        this.getLatestReleaseCallback = ((owner, repo, handler) -> {
            testContext.assertEquals("Trumeet", owner);
            testContext.assertEquals("MiPushTester", repo);
            async.countDown();
        });

        vertx.createHttpClient().get(8080, "localhost", ApiVerticle.ROUTE_UPDATE,
                httpClientResponse -> {
                    int status = httpClientResponse.statusCode();
                    if (status == NO_CONTENT.code()) status = 200;
                    testContext.assertEquals(200, status);
                    async.countDown();
                    async.complete();
                })
                .putHeader(Constants.HEADER_PRODUCT, Constants.TESTER_CLIENT_ID)
                .end();

        Thread.sleep(1000);

        this.getLatestReleaseCallback = ((owner, repo, handler) -> {
            // Shouldn't be called
            testContext.fail("GitHub API is called by an \"unauthorized\" client");
        });

        vertx.createHttpClient().get(8080, "localhost", ApiVerticle.ROUTE_UPDATE, httpClientResponse -> {
                    testContext.assertEquals(204, httpClientResponse.statusCode());
                    async.countDown();
                    async.complete();
                })
                .putHeader(Constants.HEADER_PRODUCT, "android.camera")
                .end();
    }

    @Test(timeout = 2000)
    public void shouldGetNonExistingUpdate (TestContext testContext) {
        Async async = testContext.async();
        this.getLatestReleaseCallback = ((owner, repo, handler) -> {
            handler.handle(new AsyncResult<HttpResponse<Release>>() {
                @Override
                public HttpResponse<Release> result() {
                    return new HttpResponse<Release>() {
                        @Override
                        public HttpVersion version() {
                            return null;
                        }

                        @Override
                        public int statusCode() {
                            return 200;
                        }

                        @Override
                        public String statusMessage() {
                            return "OK";
                        }

                        @Override
                        public MultiMap headers() {
                            return null;
                        }

                        @Override
                        public String getHeader(String s) {
                            return null;
                        }

                        @Override
                        public MultiMap trailers() {
                            return null;
                        }

                        @Override
                        public String getTrailer(String s) {
                            return null;
                        }

                        @Override
                        public List<String> cookies() {
                            return null;
                        }

                        @Override
                        public Release body() {
                            return null;
                        }

                        @Override
                        public Buffer bodyAsBuffer() {
                            return null;
                        }

                        @Override
                        public JsonArray bodyAsJsonArray() {
                            return null;
                        }
                    };
                }

                @Override
                public Throwable cause() {
                    return null;
                }

                @Override
                public boolean succeeded() {
                    return true;
                }

                @Override
                public boolean failed() {
                    return false;
                }
            });
        });

        vertx.createHttpClient().get(8080, "localhost", ApiVerticle.ROUTE_UPDATE,
                httpClientResponse -> {
                    testContext.assertEquals(NO_CONTENT.code(), httpClientResponse.statusCode());
                    async.complete();
                })
                .putHeader(Constants.HEADER_PRODUCT, Constants.TESTER_CLIENT_ID)
                .end();
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
        final String packageName = "android.sms";
        final boolean global = true;
        final boolean passThrough = true;
        final boolean passThroughNotification = false;

        // TODO: Test multiple situations for variety arguments (e.g. display)
        this.sendPushCallback = (Message message, String[] regIds, Map<String, String> customExtras, boolean useGlobal, Handler<AsyncResult<HttpResponse<SendMessageResponse>>> handler) -> {
                testContext.assertNotNull(message);
                if (!passThrough || passThroughNotification) {
                    testContext.assertEquals(message.getTitle(), Resources.getString("push_title", Locale.ENGLISH));
                    testContext.assertEquals(message.getTicker(), Resources.getString("push_ticker", Locale.ENGLISH));
                    // TODO: Fully match the description
                    testContext.assertNotNull(message.getDescription());
                    testContext.assertNotEquals(message.getDescription(), "");
                }
                testContext.assertEquals(message.getRestrictedPackageName(), packageName);
                testContext.assertNotNull(message.getPayload());
                testContext.assertEquals(message.getPassThrough(), passThrough ? Message.PASS_THROUGH_ENABLED : Message.PASS_THROUGH_DISABLED);
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
                testContext.assertEquals(global, useGlobal);
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
        request.setGlobal(global);
        request.setPassThroughNotification(passThroughNotification);
        vertx.createHttpClient().post(8080, "localhost", ApiVerticle.ROUTE_TEST, httpClientResponse -> {
            async.countDown();
        })
                .putHeader("Content-Type", "application/json")
                .putHeader("Accept-Language", Locale.ENGLISH.toString())
                .putHeader(Constants.HEADER_LOCALE, requestLocale)
                .putHeader(Constants.HEADER_VERSION, requestVersion)
                .putHeader(Constants.HEADER_PRODUCT, packageName)
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
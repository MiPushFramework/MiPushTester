package moe.yuuta.server.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.RoutingContext;
import moe.yuuta.server.mipush.MiPushApi;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {
    private ApiVerticle apiVerticle;
    private Vertx vertx;
    private ApiHandler stubApiHandler;

    @Before
    public void setUp (TestContext testContext) {
        vertx = Vertx.vertx();
        stubApiHandler = new ApiHandler() {
            @Override
            public void handlePush(RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleFrameworkIndex(RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleTesterIndex(RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public MiPushApi getMiPushApi() {
                return null;
            }
        };
        apiVerticle = Mockito.spy(new ApiVerticle());
        Mockito.when(apiVerticle.getApiHandler()).thenReturn(stubApiHandler);
        vertx.deployVerticle(apiVerticle, testContext.asyncAssertSuccess());
    }

    @Test(timeout = 2000)
    public void shouldGetIndex (TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE, response -> {
            testContext.assertEquals(response.statusCode(), NO_CONTENT.code());
            async.complete();
        });
    }

    @Test(timeout = 2000)
    public void shouldGetTesterIndex (TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE_TEST, response -> {
            testContext.assertEquals(response.statusCode(), NO_CONTENT.code());
            async.complete();
        });
    }

    @Test(timeout = 2000)
    public void shouldPush (TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().post(8080, "localhost", ApiVerticle.ROUTE_TEST, response -> {
            testContext.assertEquals(response.statusCode(), NO_CONTENT.code());
            async.complete();
        }).end();
    }

    @After
    public void tearDown (TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }
}

package moe.yuuta.server.api;

import org.jetbrains.annotations.NotNull;
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
            public void handlePush(@NotNull RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleFrameworkIndex(@NotNull RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleTesterIndex(@NotNull RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleUpdate(@NotNull RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }

            @Override
            public void handleGetTopicList(@NotNull RoutingContext routingContext) {
                routingContext.response().setStatusCode(NO_CONTENT.code()).end();
            }
        };
        apiVerticle = Mockito.spy(new ApiVerticle());
        // It will be called BEFORE started, so we have to mock it before deploying
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

    @Test(timeout = 2000)
    public void shouldGetUpdate (TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE_UPDATE, response -> {
            testContext.assertEquals(response.statusCode(), NO_CONTENT.code());
            async.complete();
        });
    }

    @Test(timeout = 2000)
    public void shouldGetTopicList (TestContext testContext) {
        Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", ApiVerticle.ROUTE_TEST_TOPIC, response -> {
            testContext.assertEquals(response.statusCode(), NO_CONTENT.code());
            async.complete();
        });
    }

    @After
    public void tearDown (TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }
}

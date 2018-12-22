package moe.yuuta.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {
    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
    }

    @Test
    public void shouldStart (TestContext context) {
        vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown (TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }
}

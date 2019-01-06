package moe.yuuta.server.topic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class TopicRegistryTest {
    private Vertx vertx;
    private TopicRegistry registry;

    private TopicExecuteVerticle mockVerticle;

    @Before
    public void setUp(TestContext testContext) {
        vertx = Vertx.vertx();
        registry = new TopicRegistry();
        registry = Mockito.spy(registry);
        mockVerticle = Mockito.spy(new TopicExecuteVerticle() {
        });
        Mockito.when(registry.getDefaultTopics()).thenReturn(Arrays.asList(new Topic("title", "description",
                "mock_topic", mockVerticle, null, null, null)));

        Async async = testContext.async();
        // Registering topic & unregistering topic and some stuff about Topic/register/unregister and TopicExecuteVerticle
        // will be tested here and tearDown(). So we needn't to test again.
        registry.init(vertx, ar -> {
            assertTrue(ar.succeeded());
            assertNull(ar.cause());
            try {
                Mockito.verify(mockVerticle, Mockito.times(1)).onRegister(Mockito.any(Future.class));
            } catch (Exception e) {
                testContext.fail(e);
            }
            async.complete();
        });
    }

    @After
    public void tearDown(TestContext testContext) {
        Async async = testContext.async();
        registry.clear(vertx, ar -> {
            assertTrue(ar.succeeded());
            assertNull(ar.cause());
            try {
                Mockito.verify(mockVerticle, Mockito.times(1)).onUnRegister(Mockito.any(Future.class));
            } catch (Exception e) {
                testContext.fail(e);
            }
            assertEquals(0, registry.allTopics().size());
            async.complete();
        });
    }

    @Test
    public void values() {
        assertNotNull(registry.values());
        assertEquals(1, registry.values().size());
        assertNotNull(registry.values().get("mock_topic"));
    }

    @Test
    public void allIds() {
        assertNotNull(registry.allIds());
        assertEquals(1, registry.allIds().size());
        assertEquals("mock_topic", registry.allIds().iterator().next());
    }

    @Test
    public void allTopics() {
        assertNotNull(registry.allIds());
        assertEquals(1, registry.allTopics().size());
        assertNotNull(new ArrayList<>(registry.allTopics()).get(0));
    }

    @Test
    public void getTopic() {
        assertNotNull(registry.getTopic("mock_topic"));
        assertEquals("mock_topic", registry.getTopic("mock_topic").getId());
    }
}
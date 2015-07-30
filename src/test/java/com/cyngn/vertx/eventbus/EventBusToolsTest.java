package com.cyngn.vertx.eventbus;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 4/24/15
 */
@RunWith(VertxUnitRunner.class)
public class EventBusToolsTest {



    @Test
    public void testSingleConsumer(TestContext context) {
        EventBus bus = Vertx.vertx().eventBus();

        AtomicInteger counter = new AtomicInteger(0);
        EventBusTools.oneShotConsumer(bus, "test_message", event -> {
            counter.incrementAndGet();
        });

        Async async = context.async();
        bus.send("test_message", new JsonObject().put("foo", "bar"));
        bus.send("test_message", new JsonObject());

        Vertx.vertx().setTimer(500, event -> {
            context.assertEquals(1, counter.get());
            async.complete();
        });
    }

    @Test
    public void testMulitConsumer(TestContext context) {
        EventBus bus = Vertx.vertx().eventBus();

        AtomicInteger counter = new AtomicInteger(0);
        EventBusTools.consumeNTimes(bus, "test_message", event -> {
            counter.incrementAndGet();
        }, 5, false);

        Async async = context.async();
        bus.send("test_message", new JsonObject().put("foo", "bar"));
        bus.send("test_message", new JsonObject());
        bus.send("test_message", new JsonObject().put("foo", "bar"));
        bus.send("test_message", new JsonObject().put("foo", "bar"));
        bus.send("test_message", new JsonObject().put("foo", "bar"));
        bus.send("test_message", new JsonObject().put("foo", "test"));

        Vertx.vertx().setTimer(500, event -> {
            context.assertEquals(5, counter.get());
            async.complete();
        });
    }
}

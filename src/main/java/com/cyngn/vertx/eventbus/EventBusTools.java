package com.cyngn.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * General functions for interacting with the event bus.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 4/24/15
 */
public class EventBusTools {

    /**
     * Listen to a message just once
     *
     * @param bus the event bus to listen on
     * @param address the address to listen for
     * @param handler callback on message received
     * @param <T>
     * @return the consumer created
     */
    public static <T> MessageConsumer<T> oneShotConsumer(EventBus bus, String address, Handler<Message<T>> handler) {
       return consumeNTimes(bus, address, handler, 1, false);
    }

    /**
     * Listen to a message just once
     *
     * @param bus the event bus to listen on
     * @param address the address to listen for
     * @param handler callback on message received
     * @param <T>
     * @return the consumer created
     */
    public static <T> MessageConsumer<T> oneShotLocalConsumer(EventBus bus, String address, Handler<Message<T>> handler) {
        return consumeNTimes(bus, address, handler, 1, true);
    }

    /**
     * Listen for a message N times
     *
     * @param bus the event bus to listen on
     * @param address the address to listen for
     * @param handler callback on message(s) received
     * @param timesToConsume the number of times to listen for a message
     * @param isLocalOnly should you consume just on the local event bus or everywhere
     * @param <T>
     * @return the consumer created
     */
    public static <T> MessageConsumer<T> consumeNTimes(EventBus bus, String address, Handler<Message<T>> handler,
                                                       int timesToConsume, boolean isLocalOnly) {
        if(timesToConsume <= 0) {return null;}

        MessageConsumer<T> consumer = isLocalOnly ? bus.localConsumer(address) : bus.consumer(address);
        AtomicInteger count = new AtomicInteger(0);
        consumer.handler(msg -> {
            try {
                handler.handle(msg);
                count.incrementAndGet();
            } finally {
                if (count.get() == timesToConsume) {
                    consumer.unregister();
                }
            }
        });
        return consumer;
    }

}

package com.cyngn.vertx.client;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ServiceClient}
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/25/15.
 */
public class ServiceClientTest {

    private Vertx vertx;

    @Before
    public void before() {
        vertx = Vertx.vertx();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicationAPIThrowException() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.addApiTimeout("api", 1000L);
        builder.addApiTimeout("api", 1000L);
    }

    @Test
    public void testGetTimeout() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.withHost("localhost").withPort(1234);
        builder.addApiTimeout("api", 1000L);
        ServiceClient serviceClient = builder.build();
        Assert.assertTrue(1000L == serviceClient.getTimeout("api"));
    }

    @Test
    public void testGetTimeoutOnNoTimeout() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.withHost("localhost").withPort(1234);
        ServiceClient serviceClient = builder.build();
        Assert.assertTrue(0L == serviceClient.getTimeout("api"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTimeout() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.withHost("localhost").withPort(1234);
        builder.addApiTimeout("api", -1000L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithoutHost() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithoutPort() {
        ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.withHost("localhost");
        builder.build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFromJsonConfigWithoutHost() {
        JsonObject config = new JsonObject();
        ServiceClient.create(vertx, config);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFromJsonConfigWithoutPort() {
        JsonObject config = new JsonObject();
        config.put(ServiceClient.HOST, "localhost");
        ServiceClient.create(vertx, config);
    }

}
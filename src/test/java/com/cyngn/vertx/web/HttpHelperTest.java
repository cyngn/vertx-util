package com.cyngn.vertx.web;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 8/21/15
 */
public class HttpHelperTest {
    @Test
    public void testIsHttpErrorResponse() {
        Assert.assertTrue(!HttpHelper.isHttp2XXResponse(199));
        Assert.assertTrue(!HttpHelper.isHttp2XXResponse(301));
        Assert.assertTrue(HttpHelper.isHttp2XXResponse(250));
        Assert.assertTrue(HttpHelper.isHttp2XXResponse(200));
        Assert.assertTrue(HttpHelper.isHttp2XXResponse(299));
    }
}

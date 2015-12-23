package com.cyngn.vertx.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 2/4/15
 */
public class JsonUtilTest {

    static class Foo {
        @JsonProperty
        public String bar;

        @JsonProperty
        public int testField;

        @JsonIgnore
        public String dontTouch;

        public Foo() {
        }
    }

    class Bar {
        public String foo;
    }

    @Test
    public void testJsonDeserialize() {
        String testStr = "{\"bar\":\"testStr\",\"testField\":5}";

        Foo f = JsonUtil.parseJsonToObject(testStr, Foo.class);

        Assert.assertEquals("testStr", f.bar);
        Assert.assertEquals(5, f.testField);
    }

    @Test
    public void testInvalidJsonDeserialize() {
        Foo f = JsonUtil.parseJsonToObject((String) null, Foo.class);

        Assert.assertNull(f);
    }

    @Test
    public void testJsonSerialize() {
        Foo f = new Foo();
        f.bar = "testStr";
        f.testField = 5;
        f.dontTouch = "notMe";

        String testStr = JsonUtil.getJsonForObject(f);

        JsonObject jsonObject = new JsonObject(testStr);
        Assert.assertTrue(jsonObject.getString("bar").equals(f.bar));
        Assert.assertTrue(!jsonObject.containsKey("dontTouch"));

        Assert.assertEquals(testStr, jsonObject.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadObjectJsonSerialize() {
        String testStr = JsonUtil.getJsonForObject(null);
    }
}

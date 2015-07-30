package com.cyngn.vertx.async;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 10/15/14
 */
public class LatchTests {

    @Test
    public void countdownTest() {
        final List<Boolean> results = new ArrayList<>();
        Latch latch = new Latch(2, () -> results.add(true));
        latch.complete();
        latch.complete();

        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0));

        latch.reset(3);
        results.clear();
        latch.complete();
        latch.complete();

        Assert.assertTrue(results.size() == 0);
    }

    @Test(expected = IllegalStateException.class)
    public void failCompleteCallsAfterCompletedTest() {
        final List<Boolean> results = new ArrayList<>();
        Latch latch = new Latch(2, () -> results.add(true));
        latch.complete();
        latch.complete();

        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0));

        latch.complete();
    }
}

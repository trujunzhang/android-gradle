package net.oschina.app.ui.main;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class MainTest {


    private Main activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Main_.class).create().get();
    }

    @Test
    public void testFragment() throws Exception {
        assertTrue(activity.fragmentHeaderTab != null);
        assertTrue(activity.fragmentFooterTab != null);
    }

    @Test
    public void testContext() throws Exception {
        assertTrue(activity.appContext != null);
    }
}
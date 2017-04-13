
package com.caine.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HotKeyActivateWindowTaskTest {
    private static final KeyStroke KEY_STROKE_F1 = KeyStroke.getKeyStroke("HOT_KEY_STRING");

    @Mock
    SearchController searchController;

    private HotKeyActivateWindowTask task;
    @Before
    public void setup() {

    }

    private void createTask(KeyStroke hotKey) {
        task = new HotKeyActivateWindowTask(searchController, hotKey);
    }

    @Test
    public void testRunTask() {

        createTask(KEY_STROKE_F1);
        task.run();
        verify(searchController).showWindowOnHotKey(KEY_STROKE_F1);
    }
}
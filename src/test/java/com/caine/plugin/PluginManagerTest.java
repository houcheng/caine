
package com.caine.plugin;

import com.caine.config.AppConfiguration;
import com.caine.ui.SearchController;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginManagerTest {

    private static final String HOT_KEY_F1 = "HOT_KEY_STRING";
    private static final KeyStroke KEY_STROKE_F1 = KeyStroke.getKeyStroke(HOT_KEY_F1);
    private static final String PLUGIN1_NAME = "pluginProxy1";
    private static final String PLUGIN2_NAME = "pluginProxy2";

    private static final String HOT_KEY_F2 = "F2";
    private static final KeyStroke KEY_STROKE_F2 = KeyStroke.getKeyStroke(HOT_KEY_F2);
    private static final String PLUGIN3_NAME = "pluginProxy3";

    @Mock
    SearchController searchController;
    @Mock
    AppConfiguration appConfiguration;

    @Mock
    PluginProxy pluginProxy1;
    @Mock
    PluginProxy pluginProxy2;
    @Mock
    PluginProxy pluginProxy3;

    private PluginManager pluginManager;

    @Test
    public void testGetBannerFromHotKey() {
        createPluginManager();

        assertThat(pluginManager.getBannerFromHotKey(KEY_STROKE_F1)).contains(PLUGIN1_NAME);
        assertThat(pluginManager.getBannerFromHotKey(KEY_STROKE_F1)).contains(PLUGIN2_NAME);
        assertThat(pluginManager.getBannerFromHotKey(KEY_STROKE_F1)).doesNotContain(PLUGIN3_NAME);

        assertThat(pluginManager.getBannerFromHotKey(KEY_STROKE_F2)).contains(PLUGIN3_NAME);
    }

    @Test
    public void testUpdateQuery() {
        createPluginManager();
        pluginManager.updateQuery(KEY_STROKE_F1, "keyword");

        verify(pluginProxy1).updateQuery("keyword");
        verify(pluginProxy2).updateQuery("keyword");
        verifyZeroInteractions(pluginProxy3);
    }

    @Test
    public void testUpdateQueryWithAnotherHotKey() {
        createPluginManager();
        pluginManager.updateQuery(KEY_STROKE_F2, "keyword");

        verifyZeroInteractions(pluginProxy1);
        verifyZeroInteractions(pluginProxy2);
        verify(pluginProxy3).updateQuery("keyword");
    }

    @Test
    public void testCancelQuery() {
        createPluginManager();
        pluginManager.cancelQuery(KEY_STROKE_F1);

        verify(pluginProxy1).cancelQuery();
        verify(pluginProxy2).cancelQuery();
        verifyZeroInteractions(pluginProxy3);
    }

    @Test
    public void testCancelQueryWithAnotherHotKey() {
        createPluginManager();
        pluginManager.cancelQuery(KEY_STROKE_F2);

        verifyZeroInteractions(pluginProxy1);
        verifyZeroInteractions(pluginProxy2);
        verify(pluginProxy3).cancelQuery();
    }

    private void createPluginManager() {
        when(appConfiguration.getHotKeys()).thenReturn(ImmutableSet.of(HOT_KEY_F1, HOT_KEY_F2));
        when(appConfiguration.getPluginListByHotKey(HOT_KEY_F1)).thenReturn(
                ImmutableList.of(PLUGIN1_NAME, PLUGIN2_NAME));
        when(appConfiguration.getPluginListByHotKey(HOT_KEY_F2)).thenReturn(
                ImmutableList.of(PLUGIN3_NAME));

        when(appConfiguration.getPluginType(any())).thenReturn("NullPlugin");

        pluginManager = new PluginManager(searchController, appConfiguration);

        pluginManager.getNameToPluginMap().put(PLUGIN1_NAME, pluginProxy1);
        pluginManager.getNameToPluginMap().put(PLUGIN2_NAME, pluginProxy2);
        pluginManager.getNameToPluginMap().put(PLUGIN3_NAME, pluginProxy3);
    }
}
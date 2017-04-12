package com.caine.plugin;

import com.caine.ui.SearchController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginProxyImplTest {

    @Mock
    private SearchController searchController;
    @Mock
    private Plugin plugin;

    private PluginProxyImpl pluginProxy;
    private Thread pluginThread;
    private int queryPollCount = 0;

    @Before
    public void setup() {
        pluginProxy = new PluginProxyImpl(searchController, plugin);
        pluginThread = new Thread(pluginProxy);
        pluginThread.start();
    }

    @After
    public void clean() {
        pluginProxy.shutdown();
    }

    @Test
    public void testUpdateQuery() throws InterruptedException {
        when(plugin.queryByPage("keyword", 0)).thenReturn(new Object[0]);
        pluginProxy.updateQuery("keyword");

        Thread.sleep(1000);
        verify(plugin).queryByPage("keyword", 0);
    }

    @Test
    public void testUpdateQueryWithEmptyString() throws InterruptedException {
        pluginProxy.updateQuery("");

        Thread.sleep(300);

        verify(plugin, never()).queryByPage(anyString(), anyInt());
    }

    @Test
    public void testUpdateQueryWithTwoPages() throws InterruptedException {

        when(plugin.queryByPage("keyword", 0)).thenReturn(new Object[0]);
        when(plugin.hasMorePage(0)).thenReturn(true);
        when(plugin.hasMorePage(1)).thenReturn(true);
        when(plugin.hasMorePage(2)).thenReturn(false);

        pluginProxy.updateQuery("keyword");
        Thread.sleep(1000);

        verify(plugin).queryByPage("keyword", 0);
        verify(plugin).queryByPage("keyword", 1);
    }


    @Test
    public void testCancelQuery() throws InterruptedException {

        when(plugin.queryByPage(eq("keyword"), anyInt())).thenReturn(new Object[0]);
        when(plugin.hasMorePage(anyInt())).thenAnswer( invocation -> {
            queryPollCount ++;
            return true;
        });

        pluginProxy.updateQuery("keyword");
        Thread.sleep(500);

        assertThat(queryPollCount).isGreaterThan(0);

        pluginProxy.cancelQuery();

        Thread.sleep(100);
        int lastPollCount = queryPollCount;

        Thread.sleep(500);
        assertThat(queryPollCount).isEqualTo(lastPollCount);

    }

    @Test
    public void testShutdown() throws InterruptedException {
        pluginProxy.shutdown();
        Thread.sleep(100);
        assertThat(pluginThread.isAlive()).isFalse();
    }
}

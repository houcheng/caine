package com.caine.plugin;

import com.caine.plugin.pluginstore.FileSearchPlugin;
import com.caine.ui.SearchController;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.*;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {

    private final Map<String, PluginProxy> pluginMap = new HashMap<>();
    private final SearchController searchController;

    @Inject
    public PluginManager(SearchController searchController) {

        this.searchController = searchController;

        tryToLoadAllPlugins();
    }

    public void updateQuery(String query) {
        for (PluginProxy pluginProxy : pluginMap.values()) {
            pluginProxy.updateQuery(query);
        }
    }

    private void tryToLoadAllPlugins() {
        List<Class> pluginClasses = tryToLoadPluginClasses();

        try {
            registerAllPlugins(pluginClasses);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private List<Class> tryToLoadPluginClasses() {
        try {
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            return loadClasses(cp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Class> loadClasses(ClassPath cp) throws ClassNotFoundException {
        List<Class> classesList = new LinkedList();
        for(ClassPath.ClassInfo info : cp.getTopLevelClasses("com.caine.plugin.pluginstore")) {
            System.out.println("Load plugin: " + info.getName());
            classesList.add(Class.forName(info.getName()));
        }

        return classesList;
    }

    // TODO: Add a history entries search plugin for accelerating search
    private void registerAllPlugins(List<Class> pluginClasses) throws IllegalAccessException, InstantiationException {
        for (Class pluginClass : pluginClasses) {
            registerPlugin(pluginClass);
        }
    }

    private void registerPlugin(Class pluginClass) throws IllegalAccessException, InstantiationException {

        Plugin plugin = (Plugin) pluginClass.newInstance();
        PluginProxy proxyPlugin  = loadPluginAndProxy(plugin);
        pluginMap.put(plugin.getName(), proxyPlugin);
    }

    private PluginProxy loadPluginAndProxy(Plugin plugin) {

        PluginProxy proxyPlugin = new PluginProxyImpl(searchController, plugin);
        new Thread(proxyPlugin).start();
        return proxyPlugin;
    }
}

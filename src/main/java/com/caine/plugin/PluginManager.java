package com.caine.plugin;

import com.caine.config.AppConfiguration;
import com.caine.ui.SearchController;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {

    private final Map<String, Class> classMap = new HashMap<>();
    private final Map<KeyStroke, List<String>> hotKeyToInstanceNames = new HashMap<>();
    private final Map<String, PluginProxy> nameToPluginMap = new HashMap<>();
    private final SearchController searchController;

    AppConfiguration appConfiguration;

    @Inject
    public PluginManager(SearchController searchController, AppConfiguration appConfiguration) {

        this.searchController = searchController;
        this.appConfiguration = appConfiguration;

        tryToLoadPluginClasses();
        loadPluginsByConfiguration();
        loadHotKeyToInstanceNamesByConfiguration();
    }
    public String getBannerFromHotKey(KeyStroke hotKey) {

        String banner = "";
        for (String instanceName : hotKeyToInstanceNames.get(hotKey)) {
            banner += instanceName;
        }
        return banner;
    }

    private void loadHotKeyToInstanceNamesByConfiguration() {
        for (String hotKey : appConfiguration.getHotKeys()) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(hotKey);
            hotKeyToInstanceNames.put(keyStroke, appConfiguration.getPluginListByHotKey(hotKey));
        }
    }

    // TODO: add cancel query

    public void updateQuery(KeyStroke hotKey, String query) {
        for (String instanceName : hotKeyToInstanceNames.get(hotKey)) {
            PluginProxy pluginProxy = nameToPluginMap.get(instanceName);
            pluginProxy.updateQuery(query);
        }
    }

    public void cancelQuery(KeyStroke hotKey) {
        for (String instanceName : hotKeyToInstanceNames.get(hotKey)) {
            PluginProxy pluginProxy = nameToPluginMap.get(instanceName);
            pluginProxy.cancelQuery();
        }
    }

    private void tryToLoadPluginClasses() {
        try {

            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            loadPluginClasses(cp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPluginClasses(ClassPath cp) throws ClassNotFoundException {

        for(ClassPath.ClassInfo info : cp.getTopLevelClasses("com.caine.plugin.pluginstore")) {
            System.out.println("Load plugin: " + info.getSimpleName());
            classMap.put(info.getSimpleName(), Class.forName(info.getName()));
        }
    }

    private void loadPluginsByConfiguration() {
        for (String hotKeys : appConfiguration.getHotKeys()) {
            loadPluginsByHotKey(hotKeys);
        }
    }

    private void loadPluginsByHotKey(String hotKeys) {
        for (String instanceName : appConfiguration.getPluginListByHotKey(hotKeys)) {
            String className = appConfiguration.getPluginType(instanceName);
            tryToLoadPlugin(instanceName, className);
        }
    }

    private void tryToLoadPlugin(String instanceName, String className) {

        Class pluginClass = classMap.get(className);
        try {
            loadPlugin(instanceName, pluginClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    // TODO: Add a history entries search plugin for accelerating search
    private void loadPlugin(String instanceName, Class pluginClass) throws IllegalAccessException, InstantiationException {

        Plugin plugin = (Plugin) pluginClass.newInstance();
        plugin.load(instanceName);

        PluginProxy proxyPlugin  = loadPluginAndProxy(plugin);
        nameToPluginMap.put(instanceName, proxyPlugin);
    }

    private PluginProxy loadPluginAndProxy(Plugin plugin) {

        PluginProxy proxyPlugin = new PluginProxyImpl(searchController, plugin);
        new Thread(proxyPlugin).start();
        return proxyPlugin;
    }

}

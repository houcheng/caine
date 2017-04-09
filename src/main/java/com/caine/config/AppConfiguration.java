package com.caine.config;

import com.google.common.io.Files;
import com.google.inject.Singleton;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application configuration object for Caine.
 */
@Singleton
public class AppConfiguration {
    private static final String HOME = System.getProperty("user.home");
    private static final String CONFIG_FILE_PATH = HOME + "/.config/caine/config.yaml";

    private Map<String, Object> yamlConfig;

    public AppConfiguration() {
        File yamlFile = new File(CONFIG_FILE_PATH);
        try {
            yamlConfig = loadYamlConfigFromFile(yamlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getHotKeys() {
        Map <String, Object> hotkeys = (Map<String, Object>) yamlConfig.get("HotKeys");
        return hotkeys.keySet();
    }

    public List<String> getPluginListByHotKeys(String hotKey) {
        Map <String, Object> hotkeys = (Map<String, Object>) yamlConfig.get("HotKeys");
        return (List<String>) hotkeys.get(hotKey);
    }

    private Map<String, Object> loadYamlConfigFromFile(File yamlFile) throws IOException {
        Yaml yaml = new Yaml();
        return (Map<String, Object>) yaml.load(Files.asByteSource(yamlFile).openStream());
    }
}


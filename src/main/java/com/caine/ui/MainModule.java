package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.plugin.PluginManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MainModule extends AbstractModule {
    SearchController controller;
    Parent root;

    @Override
    protected void configure() {
        bind(QueryClient.class);
        bind(PluginManager.class);
    }

    @Provides
    public SearchController controller() throws IOException {
        createSearchControllerSingleton();
        return controller;
    }

    @Provides
    public Parent parent() throws IOException {
        createSearchControllerSingleton();
        return root;
    }

    private void createSearchControllerSingleton() throws IOException {
        if(root == null) {
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(getClass().getResource("/SearchWindow.fxml"));
            controller = loader.getController();
        }
    }
}

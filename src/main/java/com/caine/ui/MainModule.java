package com.caine.ui;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

import java.io.IOException;

public class MainModule extends AbstractModule {
    private FXMLLoader loader;

    @Override
    protected void configure() {
    }

    @Provides
    public Parent parent() throws IOException {

        // FXMLLoader getController issue: the URL must be provided during construction.
        loader = new FXMLLoader(getClass().getResource("/SearchWindow.fxml"));
        return loader.load();
    }

    @Provides
    public SearchController controller() throws IOException {

        SearchController controller = loader.getController();
        return controller;
    }

    @Provides
    public ListView listView() throws IOException {

        return controller().getListView();
    }

}

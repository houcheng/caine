package com.caine.ui;

import com.caine.plugin.PluginManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApplication extends Application {

    private Injector injector;
    private Parent root;
    private SearchController searchController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        createGuiceInstances();
        configureSearchController(primaryStage);
        displayPrimaryStage(primaryStage);
    }

    private void displayPrimaryStage(Stage primaryStage) {

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void createGuiceInstances() {

        injector = Guice.createInjector(new MainModule());

        root = injector.getInstance(Parent.class);
        searchController = injector.getInstance(SearchController.class);
        injector.getInstance(PluginManager.class);
    }

    private void configureSearchController(Stage primaryStage) {

        injector.injectMembers(searchController);
        searchController.setStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

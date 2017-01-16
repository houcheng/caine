package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.plugin.PluginManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Injector injector = Guice.createInjector(new MainModule());
        // create UI and controller
        Parent root = injector.getInstance(Parent.class);

        // inject member on instance not created by Guice
        injector.injectMembers(injector.getInstance(SearchController.class));

        // create plugin
        injector.getInstance(PluginManager.class);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

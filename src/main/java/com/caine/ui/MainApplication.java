package com.caine.ui;

import com.caine.plugin.PluginManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;

public class MainApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Injector injector = Guice.createInjector(new MainModule());
        // create UI and controller
        Parent root = injector.getInstance(Parent.class);

        // inject member on instance not created by Guice
        injector.injectMembers(injector.getInstance(SearchController.class));
        injector.getInstance(SearchController.class).setStage(primaryStage);

        // create plugin
        injector.getInstance(PluginManager.class);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}

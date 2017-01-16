package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.plugin.PluginManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

public class MainApplication extends Application {

    private Provider keyProvider;
    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Injector injector = Guice.createInjector(new MainModule());
        // create UI and controller
        Parent root = injector.getInstance(Parent.class);

        // inject member on instance not created by Guice
        injector.injectMembers(injector.getInstance(SearchController.class));

        // create plugin
        injector.getInstance(PluginManager.class);

        this.primaryStage = primaryStage;
        registerHotKey();
        scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void registerHotKey() {
        keyProvider = Provider.getCurrentProvider(false);
        HotKeyListener keyListener = new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setScene(scene);
                        primaryStage.show();
                    }
                });
            }
        };

        keyProvider.register(KeyStroke.getKeyStroke("F1"), keyListener);
        keyProvider.register(KeyStroke.getKeyStroke("F2"), keyListener);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

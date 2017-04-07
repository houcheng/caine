package com.caine.ui;

import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.caine.plugin.PluginManager;
import com.google.inject.Inject;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A JavaFX UI controller to handle UI events from SearchWindow.xml and update query
 * results onto SearchListOrganizer
 */
public class SearchController implements Initializable {

    private static final int MINIMUM_QUERY_STRING_LENGTH = 3;

    @FXML
    public TextField inputTextField;

    @Getter
    @FXML
    public ListView<String> listView;

    private Stage stage;

    private PluginManager queryClient;
    private SearchListOrganizer searchListOrganizer;

    private String queryString;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        registerHotKey();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        Scene scene = SearchController.this.stage.getScene();
    }

    @Inject
    public void injectDependencyByGuice(PluginManager client, SearchListOrganizer searchListOrganizer) {
        this.queryClient = client;
        this.searchListOrganizer = searchListOrganizer;
    }

    public void handleKeyTypedOnTextField(KeyEvent keyEvent) {

        this.queryString = inputTextField.getText();
        if (this.queryString.length() >= MINIMUM_QUERY_STRING_LENGTH) {
            queryClient.updateQuery(inputTextField.getText());
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {

        System.out.println(keyEvent.toString());

        if(keyEvent.getCode() == KeyCode.UP) {
            searchListOrganizer.changeListSelectedItem(-1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            searchListOrganizer.changeListSelectedItem(1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.ENTER) {
            openQueryResult(searchListOrganizer.getCurrentQueryResult());
            clearHideUI();
            keyEvent.consume();
        }
    }

    public void handleMouseClickedOnList(MouseEvent mouseEvent) {
        searchListOrganizer.updateCurrentIndexByListSelection();
    }

    private void openQueryResult(QueryResult selectedResult) {
        if (selectedResult == null) {
            return;
        }

        String uri = selectedResult.getHandleUri();
        if (uri.startsWith("http:") || uri.startsWith("https:") || uri.startsWith("ftp:")) {
            openUri(uri);
        } else if (uri.startsWith("app:")) {

        } else {
            openFile(uri);
        }
    }

    /**
     * File open reference
     * http://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform
     */
    private void openFile(String path) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux") || osName.contains("unix")) {
            openFileByUtility("xdg-open", path);
        } else if (osName.contains("mac")) {
            openFileByUtility("open", path);
        } else if (osName.contains("windows")) {
            openFileByUtility("explorer", path);
        }
    }

    private void openUri(String uri) {
        try {
            Desktop.getDesktop().browse(URI.create(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFileByUtility(String utility, String path) {
        try {
            Runtime.getRuntime().exec(utility + "  " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearHideUI() {
        inputTextField.setText("");
        searchListOrganizer.updateQueryString("");
        stage.hide();
    }

    private void registerHotKey() {
        // It makes runLater works.
        Platform.setImplicitExit(false);

        Provider keyProvider = Provider.getCurrentProvider(false);
        HotKeyListener keyListener = createHotKeyListener(new ActivateWindowThread());
        keyProvider.register(KeyStroke.getKeyStroke("F1"), keyListener);
        keyProvider.register(KeyStroke.getKeyStroke("F2"), keyListener);
    }

    private HotKeyListener createHotKeyListener(ActivateWindowThread thread) {
        return new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                Platform.runLater(thread);
            }
        };
    }

    public void appendSearchResult(String queryString, QueryResultGenerator results) {

        if(! this.queryString.equals(queryString)) {
            return;
        }

        appendQueryResultInUIThread(queryString, results);
    }

    private void appendQueryResultInUIThread(final String queryString, final QueryResultGenerator results) {
        Platform.runLater(createAppendQueryResultJob(queryString, results));
    }

    private Runnable createAppendQueryResultJob(final String queryString, final QueryResultGenerator results) {

        return new Runnable() {
            @Override
            public void run() {
                searchListOrganizer.appendQueryResult(queryString, results);
            }
        };
    }

    class ActivateWindowThread implements Runnable {

        Stage stage;

        @Override
        public void run() {

            updateStageFromSearchController();
            activateWindow();
        }

        private void updateStageFromSearchController() {

            sleepInMilliSecond(300);
            stage = SearchController.this.stage;
        }

        // JavaFx request focus issue is not resolved.
        // https://bugs.openjdk.java.net/browse/JDK-8120102
        // TODO: call X11 library to activate window, reference wmctrl utility.
        private void activateWindow() {
            Stage windowStage = (Stage) stage.getScene().getWindow();

            windowStage.show();
            windowStage.toFront();

            stage.setAlwaysOnTop(true);
            windowStage.setAlwaysOnTop(true);

            stage.requestFocus();
            windowStage.requestFocus();
            inputTextField.requestFocus();

        }

        private void sleepInMilliSecond(int milliSecond) {
            try {
                Thread.sleep(milliSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

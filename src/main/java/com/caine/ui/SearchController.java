package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles UI events and updates query results with help of SearchListOrganizer.
 */
public class SearchController implements Initializable {

    private static final int MINIMUM_QUERY_STRING_LENGTH = 3;

    @FXML
    public TextField inputTextField;

    @Getter
    @FXML
    public ListView<String> listView;

    private Stage stage;
    private Stage secondStage;

    private QueryClient queryClient;
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
        secondStage = new Stage(StageStyle.UTILITY);
        secondStage.initModality(Modality.APPLICATION_MODAL);
    }

    @Inject
    public void updateDependency(QueryClient client, SearchListOrganizer searchListOrganizer) {
        this.queryClient = client;
        this.searchListOrganizer = searchListOrganizer;
    }

    public void handleKeyTyped(KeyEvent keyEvent) {
        this.queryString = inputTextField.getText();
        if (this.queryString.length() >= MINIMUM_QUERY_STRING_LENGTH) {
            queryClient.updateQuery(inputTextField.getText());
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.UP) {
            searchListOrganizer.updateListIndexSelection(-1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            searchListOrganizer.updateListIndexSelection(1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.ENTER) {
            openQueryResult(searchListOrganizer.selectListItem());
            clearHideUI();
            keyEvent.consume();
        }
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
     * Open file for various operation systems. The windows and mac open utility reference this
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
        searchListOrganizer.clearListViews("");
        stage.hide();
    }

    private void registerHotKey() {
        // To make runLater works.
        Platform.setImplicitExit(false);

        Provider keyProvider = Provider.getCurrentProvider(false);
        HotKeyListener keyListener = new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage.show();
                        stage.toFront();
                        stage.hide();
                        stage.show();
                        stage.requestFocus();
                        stage.setAlwaysOnTop(true);
                        try {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //remember the last location of mouse
                            final Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();

                            //simulate a mouse click on title bar of window
                            Robot robot = new Robot();

                            robot.mouseMove((int) stage.getScene().getWindow().getX() + 30,
                                    (int) stage.getScene().getWindow().getY() + 10);
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            //move mouse to old location
                            // robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        keyProvider.register(KeyStroke.getKeyStroke("F1"), keyListener);
        keyProvider.register(KeyStroke.getKeyStroke("F2"), keyListener);
    }


    public void appendSearchResult(String queryString, QueryResultGenerator results) {
        if(! this.queryString.equals(queryString)) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                searchListOrganizer.appendQueryResult(queryString, results);
            }
        });
    }

}

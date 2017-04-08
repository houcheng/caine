package com.caine.ui;

import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.caine.exception.WindowNotFoundException;
import com.caine.plugin.PluginManager;
import com.google.inject.Inject;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.stage.Window;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import static com.caine.ui.MainApplication.APPLICATION_WINDOW_NAME;
import static java.lang.Integer.min;

/**
 * A JavaFX UI controller to handle UI events from SearchWindow.xml and update query
 * results onto SearchListOrganizer
 */
public class SearchController implements Initializable {
    private static final int MINIMUM_QUERY_STRING_LENGTH = 3;

    private final double WINDOW_WIDTH_TO_DESKTOP = 0.6;
    private final double WINDOW_HEIGHT_TO_DESKTOP = 0.9;

    @FXML
    public TextField inputTextField;

    @Getter
    @FXML
    public ListView<String> listView;

    private Stage stage;

    private PluginManager queryClient;
    private SearchListOrganizer searchListOrganizer;

    private String queryString;
    private int listViewSize;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        HBox.setHgrow(listView, Priority.ALWAYS);
        VBox.setVgrow(listView, Priority.ALWAYS);

        registerHotKey();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Inject
    public void injectDependencyByGuice(PluginManager client, SearchListOrganizer searchListOrganizer) {
        this.queryClient = client;
        this.searchListOrganizer = searchListOrganizer;
    }

    public void handleKeyTypedOnTextField(KeyEvent keyEvent) {

        this.queryString = inputTextField.getText();
        System.out.println("The listview height is:" + this.listView.getHeight());
        if (this.queryString.length() >= MINIMUM_QUERY_STRING_LENGTH) {
            queryClient.updateQuery(inputTextField.getText());
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {

        if(keyEvent.getCode() == KeyCode.UP) {
            searchListOrganizer.changeListSelectedItem(-1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            searchListOrganizer.changeListSelectedItem(1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.PAGE_DOWN) {
            searchListOrganizer.changeListSelectedItem(listViewSize);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.PAGE_UP) {
            searchListOrganizer.changeListSelectedItem(-listViewSize);
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

    int getListViewIndex() {
        ListViewSkin<?> skin = (ListViewSkin<?>) listView.getSkin();
        VirtualFlow<?> vf = (VirtualFlow<?>) skin.getChildren().get(0);
        System.out.printf("The index is:%d\n", vf.getFirstVisibleCell().getIndex());
        return vf.getFirstVisibleCell().getIndex();
    }

    int getListViewSize() {
        return listViewSize;
    }

    public void updateWindowSizeByItemNumber(int itemCount) {

        double lineHeight = inputTextField.getHeight() - 2;
        double maxHeight = Screen.getPrimary().getVisualBounds().getHeight() * WINDOW_HEIGHT_TO_DESKTOP;
        listViewSize = min((int) (maxHeight/lineHeight) - 1 , itemCount);

        stage.getScene().getWindow().setHeight(lineHeight * listViewSize + inputTextField.getHeight());
    }

    public void setWindowInitialHeightPosition() {
        Window searchWindow = listView.getParent().getScene().getWindow();

        double initialHeight = inputTextField.getHeight();
        searchWindow.setHeight(initialHeight);

        double desktopHeight = Screen.getPrimary().getVisualBounds().getHeight();
        searchWindow.setY(desktopHeight * (1.0 - WINDOW_HEIGHT_TO_DESKTOP) / 2);

        String styles = getClass().getResource("/SearchWindowStyles.css").toExternalForm();
        stage.getScene().getStylesheets().add(styles);
    }

    public void setWindowWidthPosition() {

        double initialWidth = Screen.getPrimary().getVisualBounds().getWidth();

        Window searchWindow = listView.getParent().getScene().getWindow();
        searchWindow.setWidth(initialWidth * WINDOW_WIDTH_TO_DESKTOP);
        searchWindow.setX(initialWidth * (1-WINDOW_WIDTH_TO_DESKTOP)/2);
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
        setWindowInitialHeightPosition();
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
        @Override
        public void run() {
            showWindow();
            activateWindowInOwnThread();
        }

        private void showWindow() {
            Stage windowStage = (Stage) stage.getScene().getWindow();
            stage.show();
            windowStage.show();

            stage.requestFocus();
            inputTextField.requestFocus();
        }

        private void activateWindowInOwnThread() {
            new Thread(createActivateWindowJob()).start();
        }

        private Runnable createActivateWindowJob() {

            return new Runnable() {
                private int MAX_RETRY_COUNT = 20;
                private int ACTIVATE_WINDOW_DELAY_TIME_IN_MS = 50;
                @Override
                public void run() {

                    int retryCount = 0;
                    while (retryCount < MAX_RETRY_COUNT) {
                        try {
                            activateApplicationWindow();
                            break;
                        } catch (WindowNotFoundException ex) {
                            retryCount ++;
                            continue;
                        }
                    }
                }

                private void activateApplicationWindow() {
                    waitUiThread(ACTIVATE_WINDOW_DELAY_TIME_IN_MS);
                    (new ActivateWindowJni()).callActivateWindow(APPLICATION_WINDOW_NAME);
                }
            };
        }

        private void waitUiThread(int milliSecond) {
            try {
                Thread.sleep(milliSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

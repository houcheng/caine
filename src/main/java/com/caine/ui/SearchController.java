package com.caine.ui;

import com.caine.config.AppConfiguration;
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

import static java.lang.Integer.min;

/**
 * A JavaFX UI controller to handle UI events from SearchWindow.xml and update query
 * results onto SearchListOrganizer
 */
public class SearchController implements Initializable {
    private static final int MINIMUM_QUERY_STRING_LENGTH = 3;

    private final double WINDOW_WIDTH_TO_DESKTOP = 0.6;
    private final double WINDOW_HEIGHT_TO_DESKTOP = 0.9;

    private Stage stage;

    @FXML
    public TextField inputTextField;

    @Getter
    @FXML
    public ListView<String> listView;

    @Inject
    private AppConfiguration appConfiguration;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private SearchListOrganizer searchListOrganizer;

    private KeyStroke hotKey;
    private String queryString;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initializeUI(Stage stage) {

        this.stage = stage;

        enablePlatformRunLater();
        registerHotKey();

        initializeUIAutoGrow();
        setWindowWidthPosition();
        setWindowInitialHeightPosition();
    }

    public void handleKeyTypedOnTextField(KeyEvent keyEvent) {

        this.queryString = inputTextField.getText();

        if (this.queryString.length() >= MINIMUM_QUERY_STRING_LENGTH) {
            pluginManager.updateQuery(hotKey, inputTextField.getText());
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {

        if(keyEvent.getCode() == KeyCode.UP) {
            searchListOrganizer.moveSelectedItem(-1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            searchListOrganizer.moveSelectedItem(1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.PAGE_DOWN) {
            searchListOrganizer.moveSelectedItemByPage(1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.PAGE_UP) {
            searchListOrganizer.moveSelectedItemByPage(-1);
            keyEvent.consume();

        } else if(keyEvent.getCode() == KeyCode.ENTER) {
            openQueryResult(searchListOrganizer.getCurrentQueryResult());
            clearHideUI();
            keyEvent.consume();
        }
    }

    public void handleMouseClickedOnList(MouseEvent mouseEvent) {
        searchListOrganizer.updateCurrentIndexByListSelection();

        if (mouseEvent.getClickCount() == 2) {
            openQueryResult(searchListOrganizer.getCurrentQueryResult());
            clearHideUI();
        }
    }

    public void updateWindowSizeByItemNumber(int itemCount) {

        double lineHeight = inputTextField.getHeight() - 2;
        double maxHeight = Screen.getPrimary().getVisualBounds().getHeight() * WINDOW_HEIGHT_TO_DESKTOP;
        int listViewSize = min((int) (maxHeight/lineHeight) - 1 , itemCount);

        stage.getScene().getWindow().setHeight(lineHeight * listViewSize + inputTextField.getHeight());
        searchListOrganizer.updateListViewSize(listViewSize);
    }

    void showWindowOnHotKey(KeyStroke hotkey) {

        stage.show();
        Stage windowStage = (Stage) stage.getScene().getWindow();
        windowStage.show();

        stage.requestFocus();
        inputTextField.requestFocus();

        this.hotKey = hotkey;

        String banner = pluginManager.getBannerFromHotKey(hotkey);
        inputTextField.setPromptText(banner);
    }

    private void setWindowInitialHeightPosition() {
        Window searchWindow = listView.getParent().getScene().getWindow();

        double initialHeight = inputTextField.getHeight();
        searchWindow.setHeight(initialHeight);

        double desktopHeight = Screen.getPrimary().getVisualBounds().getHeight();
        searchWindow.setY(desktopHeight * (1.0 - WINDOW_HEIGHT_TO_DESKTOP) / 2);

        String styles = getClass().getResource("/SearchWindowStyles.css").toExternalForm();
        stage.getScene().getStylesheets().add(styles);
    }

    private void setWindowWidthPosition() {

        double initialWidth = Screen.getPrimary().getVisualBounds().getWidth();

        Window searchWindow = listView.getParent().getScene().getWindow();
        searchWindow.setWidth(initialWidth * WINDOW_WIDTH_TO_DESKTOP);
        searchWindow.setX(initialWidth * (1-WINDOW_WIDTH_TO_DESKTOP)/2);
    }

    private void initializeUIAutoGrow() {

        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        HBox.setHgrow(listView, Priority.ALWAYS);
        VBox.setVgrow(listView, Priority.ALWAYS);
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
            String [] commandLine = { utility, path };
            Runtime.getRuntime().exec(commandLine);
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

        Provider keyProvider = Provider.getCurrentProvider(false);
        HotKeyListener keyListener = createHotKeyListener();

        for (String hotKey : appConfiguration.getHotKeys()) {
            keyProvider.register(KeyStroke.getKeyStroke(hotKey), keyListener);
        }

        this.hotKey = KeyStroke.getKeyStroke(appConfiguration.getDefaultHotKey());
    }

    // Configure to make platform runLater works.
    private void enablePlatformRunLater() {
        Platform.setImplicitExit(false);
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

    private HotKeyListener createHotKeyListener() {
        return new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                Runnable thread = new HotKeyActivateWindowTask(SearchController.this, hotKey.keyStroke);
                Platform.runLater(thread);
            }
        };
    }

}

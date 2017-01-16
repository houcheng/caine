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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
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
    public TextField input;
    @Getter
    @FXML
    public ListView<String> listView;

    private Provider keyProvider;
    private HotKeyListener keyListener;

    private QueryClient client;
    private SearchListOrganizer searchListOrganizer;

    private String queryString;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setHgrow(input, Priority.ALWAYS);

    }

    @Inject
    public void updateDependency(QueryClient client, SearchListOrganizer searchListOrganizer) {
        this.client = client;
        this.searchListOrganizer = searchListOrganizer;
    }

    public void handleKeyTyped(KeyEvent keyEvent) {
        this.queryString = input.getText();
        if (this.queryString.length() >= MINIMUM_QUERY_STRING_LENGTH) {
            client.updateQuery(input.getText());
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
        input.setText("");
        searchListOrganizer.clearListViews("");
        listView.getScene().getWindow().hide();
    }

    public void appendSearchResult(String queryString, QueryResultGenerator results) {
        if(! this.queryString.equals(queryString)) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                searchListOrganizer.appendSearchResultGUI(queryString, results);
            }
        });
    }
}

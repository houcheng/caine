package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.core.QueryResultGenerator;
import com.google.inject.Inject;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles UI events and updates query results with help of SearchListOrganizer.
 */
public class SearchController implements Initializable {
    @FXML
    public TextField input;
    @Getter
    @FXML
    public ListView<String> listView;
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
        client.updateQuery(input.getText());
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.UP) {
            System.out.println("up");
            searchListOrganizer.updateListIndexSelection(-1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            System.out.println("down");
            searchListOrganizer.updateListIndexSelection(1);
            keyEvent.consume();
        }
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

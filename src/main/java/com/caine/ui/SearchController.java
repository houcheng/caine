package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.caine.plugin.PluginManager;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    @FXML
    public TextField input;
    @Getter
    @FXML
    public ListView<String> listView;

    private String queryString;
    private QueryClient client;

    private List<QueryResult> listResults = new LinkedList<>();
    private ObservableList<String> listItems = FXCollections.observableArrayList();
    private String listQueryString;
    private int listIndex = -1;

    public SearchController() {
        System.out.println("controller created");
    }
    public void handleKeyTyped(KeyEvent keyEvent) {
        this.queryString = input.getText();
        client.updateQuery(input.getText());
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.UP) {
            System.out.println("up");
            updateListIndexSelection(listIndex - 1);
            keyEvent.consume();
        } else if(keyEvent.getCode() == KeyCode.DOWN) {
            System.out.println("down");
            updateListIndexSelection(listIndex + 1);
            keyEvent.consume();
        }
    }

    private void updateListIndexSelection(int newIndex) {
        if (newIndex < 0 || newIndex > listView.getItems().size()) {
            return;
        }

        listIndex = newIndex;
        listView.getFocusModel().focus(listIndex);
        // listView.scrollTo(listIndex);
        listView.getSelectionModel().select(listIndex);
    }

    public void appendSearchResult(String queryString, QueryResultGenerator results) {
        if(! this.queryString.equals(queryString)) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SearchController.this.appendSearchResultGUI(queryString, results);
            }
        });
    }

    public void updateDependency(QueryClient client) {
        this.client = client;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setHgrow(input, Priority.ALWAYS);
    }

    private void clearListViews(String queryString) {
        listQueryString = queryString;
        listIndex = -1;
        listItems.clear();
        listResults.clear();
        listView.setItems(listItems);
        listView.setVisible(true);
    }

    private void appendSearchResultGUI(String queryString, QueryResultGenerator results) {
        if(! queryString.equals(listQueryString)) {
            clearListViews(this.queryString);
        }

        int i = 0;
        for (QueryResult result: results.getResults()) {
            i ++;
            if (i % 5 == 0) {
                listItems.add(0, "00" + result.getDisplayText());
            } else {
                listItems.add(result.getDisplayText());
            }
            listResults.add(result);
        }
    }

}

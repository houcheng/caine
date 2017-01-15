package com.caine.ui;

import com.caine.core.QueryClient;
import com.caine.plugin.PluginManager;
import com.caine.plugin.ThreadBasePlugin;
import com.caine.plugin.RubyPlugin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    @FXML
    public TextField input;
    @FXML
    public ListView<String> listView;

    private String queryString;
    private QueryClient client;

    public void testInput(KeyEvent keyEvent) {
        this.queryString = input.getText();
        client.updateQuery(input.getText());
        System.out.println("input event");
    }

    public void appendSearchResult(String queryString, List<String> results) {
        if(! this.queryString.equals(queryString)) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SearchController.this.appendSearchResultGUI(results);
            }
        });
    }

    private void appendSearchResultGUI(List<String> results) {
        listView.setVisible(true);
        ObservableList<String> items = FXCollections.observableArrayList(results);
        listView.setItems(items);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new QueryClient(this);
        PluginManager manager = new PluginManager(this, client);
        manager.load();

        listView.setVisible(false);
        HBox.setHgrow(input, Priority.ALWAYS);
    }
}

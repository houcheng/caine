package com.caine.ui;

import com.caine.core.HistoryLookupTable;
import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles content and UI status update of search list view.
 */
@Singleton
public class SearchListOrganizer {

    private final ListView resultLiveView;
    private final HistoryLookupTable historyLookupTable;

    private ObservableList<String> observableResultList = FXCollections.observableArrayList();
    private List<QueryResult> queryResultList = new LinkedList<>();

    private String currentQueryString;
    private int currentIndex = -1;

    @Inject
    public SearchListOrganizer(ListView listView, HistoryLookupTable historyLookupTable) {
        this.resultLiveView = listView;
        this.historyLookupTable = historyLookupTable;
    }

    public void updateCurrentIndexByListSelection() {
        currentIndex = resultLiveView.getSelectionModel().getSelectedIndex();
    }

    public void changeListSelectedItem(int offset) {

        updateCurrentIndex(currentIndex + offset);
        updateListSelectionByCurrentIndex();
    }

    public void updateQueryString(String queryString) {

        currentQueryString = queryString;
        clearResultList();
    }

    public void appendQueryResult(String queryString, QueryResultGenerator results) {

        resultLiveView.getParent().getScene().getWindow().setHeight(500);
        if(! queryString.equals(currentQueryString)) {
            updateQueryString(queryString);
        }

        for (QueryResult result: results.getResults()) {
            appendQueryResultItem(result);
        }
    }

    private void updateListSelectionByCurrentIndex() {
        resultLiveView.getFocusModel().focus(currentIndex);
        resultLiveView.getSelectionModel().select(currentIndex);
    }

    private void updateCurrentIndex(int newIndex) {
        if (newIndex < 0 || newIndex > resultLiveView.getItems().size()) {
            return;
        }
        currentIndex = newIndex;
    }

    private void clearResultList() {
        currentIndex = -1;
        observableResultList.clear();
        queryResultList.clear();
        resultLiveView.setItems(observableResultList);
    }

    private void appendQueryResultItem(QueryResult result) {

        observableResultList.add(result.getDisplayText());
        queryResultList.add(result);
    }

    public QueryResult getCurrentQueryResult() {

        if (currentIndex >= 0) {
            QueryResult result = queryResultList.get(currentIndex);
            historyLookupTable.access(result.getHandleUri());
            return result;
        }

        return null;
    }
}

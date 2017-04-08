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

import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 * Handles content and UI status update of search list view.
 */
@Singleton
public class SearchListOrganizer {

    private final HistoryLookupTable historyLookupTable;
    private final SearchController searchController;
    private final ListView resultLiveView;

    private ObservableList<String> observableResultList = FXCollections.observableArrayList();
    private List<QueryResult> queryResultList = new LinkedList<>();

    private String currentQueryString;
    private int currentIndex = -1;

    @Inject
    public SearchListOrganizer(HistoryLookupTable historyLookupTable, SearchController searchController) {
        this.historyLookupTable = historyLookupTable;
        this.resultLiveView = searchController.getListView();
        this.searchController = searchController;
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

        if(! queryString.equals(currentQueryString)) {
            updateQueryString(queryString);
        }

        int itemCount = 0;
        for (QueryResult result: results.getResults()) {
            appendQueryResultItem(result);
            itemCount ++;
        }

        searchController.updateWindowSizeByItemNumber(itemCount);
    }

    private void updateListSelectionByCurrentIndex() {
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

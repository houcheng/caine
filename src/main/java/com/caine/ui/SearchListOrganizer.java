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

    private final HistoryLookupTable historyLookupTable;
    private final SearchController searchController;
    private final ListView listView;

    private ObservableList<String> observableResultList = FXCollections.observableArrayList();
    private List<QueryResult> queryResultList = new LinkedList<>();

    private String currentQueryString;
    private int currentIndex = -1;

    @Inject
    public SearchListOrganizer(HistoryLookupTable historyLookupTable, SearchController searchController) {
        this.historyLookupTable = historyLookupTable;
        this.listView = searchController.getListView();
        this.searchController = searchController;
    }

    public void updateCurrentIndexByListSelection() {
        currentIndex = listView.getSelectionModel().getSelectedIndex();
    }

    public void changeListSelectedItem(int offset) {
        int newIndex = calculateNewIndexByOffset(offset);
        updateListViewByIndex(newIndex);
    }

    private int calculateNewIndexByOffset(int offset) {

        int newIndex = currentIndex + offset;

        if (newIndex < 0) {
            return 0;
        }
        if (newIndex > listView.getItems().size()) {
            return listView.getItems().size() - 1;
        }

        return newIndex;
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

    private void updateListViewByIndex(int newIndex) {
        if (newIndex == currentIndex) {
            return;
        }
        currentIndex = newIndex;

        listView.getSelectionModel().select(currentIndex);

        if(currentIndex < searchController.getListViewIndex()) {
            listView.scrollTo(currentIndex);
        } else if( currentIndex > (searchController.getListViewIndex() + searchController.getListViewSize())) {
            listView.scrollTo(currentIndex - searchController.getListViewSize());
        }

    }

    private void clearResultList() {
        currentIndex = -1;
        observableResultList.clear();
        queryResultList.clear();
        listView.setItems(observableResultList);
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

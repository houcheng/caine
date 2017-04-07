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

    public void clear(String queryString) {

        currentQueryString = queryString;
        currentIndex = -1;

        observableResultList.clear();
        queryResultList.clear();

        resultLiveView.setItems(observableResultList);
    }

    // TODO: We'll need a core search plugin that search on history entries.
    // The query result should add "keywords" field for this feature.
    public void appendQueryResult(String queryString, QueryResultGenerator results) {

        if(! queryString.equals(currentQueryString)) {
            clear(queryString);
        }

        for (QueryResult result: results.getResults()) {
            appendQueryResultItem(result);
        }
    }

    private void appendQueryResultItem(QueryResult result) {

        long priority = historyLookupTable.getLastAccessDate(result.getHandleUri());

        if (priority > 0) {
            observableResultList.add(0, "[" + result.getDisplayText() + "]" );
            queryResultList.add(0, result);

        } else {
            observableResultList.add(result.getDisplayText());
            queryResultList.add(result);
        }
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

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
 *
 */
@Singleton
public class SearchListOrganizer {
    private final ListView listView;
    private final HistoryLookupTable historyLookupTable;


    private List<QueryResult> listResults = new LinkedList<>();
    private ObservableList<String> listItems = FXCollections.observableArrayList();
    private String listQueryString;
    private int listIndex = -1;

    @Inject
    public SearchListOrganizer(ListView listView, HistoryLookupTable historyLookupTable) {
        this.listView = listView;
        this.historyLookupTable = historyLookupTable;
    }


    public void updateListIndexSelection(int indexDiff) {
        int newIndex = listIndex + indexDiff;
        if (newIndex < 0 || newIndex > listView.getItems().size()) {
            return;
        }

        listIndex = newIndex;
        listView.getFocusModel().focus(listIndex);
        // listView.scrollTo(listIndex);
        listView.getSelectionModel().select(listIndex);
    }

    public void clearListViews(String queryString) {
        listQueryString = queryString;
        listIndex = -1;
        listItems.clear();
        listResults.clear();
        listView.setItems(listItems);
    }

    // TODO: We'll need a core search plugin that search on history entries.
    // The query result should add "keywords" field for this feature.
    public void appendSearchResultGUI(String queryString, QueryResultGenerator results) {
        if(! queryString.equals(listQueryString)) {
            clearListViews(queryString);
        }

        for (QueryResult result: results.getResults()) {
            long priority = historyLookupTable.getLastAccessDate(result.getHandleUri());
            if (priority > 0) {
                listItems.add(0, "[" + result.getDisplayText() + "]" );
                listResults.add(0, result);
            } else {
                listItems.add(result.getDisplayText());
                listResults.add(result);
            }
        }
    }

    public QueryResult selectListItem() {
        if (listIndex >= 0) {
            QueryResult result = listResults.get(listIndex);
            historyLookupTable.access(result.getHandleUri());
            return result;
        }
        return null;
    }
}

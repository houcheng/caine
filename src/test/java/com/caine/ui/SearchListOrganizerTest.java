package com.caine.ui;

import com.caine.core.HistoryLookupTable;
import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.google.common.collect.ImmutableList;
import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ListView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO: Mock final methods of listView and modelSelection classes for complete tests.
 */
@RunWith(JfxRunner.class)
public class SearchListOrganizerTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private HistoryLookupTable historyLookupTable;
    @Mock
    private SearchController searchController;
    @Mock
    private ListView listView;

    @Mock
    private QueryResultGenerator queryResultGenerator;
    private QueryResult queryResult;

    private SearchListOrganizer searchListOrganizer;

    @Before
    public void setup() {
        new JFXPanel();

        when(searchController.getListView()).thenReturn(listView);
        queryResult = QueryResult.builder()
                .displayIcon("")
                .displayText("")
                .handleUri("")
                .build();
        when(queryResultGenerator.getResults()).thenReturn(ImmutableList.of(queryResult));
        searchListOrganizer = new SearchListOrganizer(historyLookupTable, searchController);

    }

    @Test
    public void testAppendQueryResult() {

        searchListOrganizer.appendQueryResult("query keywords", queryResultGenerator);

        verify(queryResultGenerator).getResults();
    }
}
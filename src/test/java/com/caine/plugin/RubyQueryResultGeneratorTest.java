package com.caine.plugin;

import com.caine.core.QueryResult;
import com.caine.ui.SearchController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RubyQueryResultGeneratorTest {

    private static final String[] RESULT_ENTRY1 = {"icon1", "text1", "url1"};
    private static final String[] RESULT_ENTRY2 = {"icon2", "text2", "url2"};

    private RubyQueryResultGenerator generator;

    @Before
    public void setup() {
    }

    @Test
    public void testGetResults() throws InterruptedException {
        Object [] results = { RESULT_ENTRY1 };
        generator = new RubyQueryResultGenerator(results);

        Iterator<QueryResult> iterator = generator.getResults().iterator();
        assertThat(iterator.hasNext()).isTrue();

        QueryResult queryResult = iterator.next();
        assertThat(queryResult.getDisplayIcon()).isEqualTo("icon1");
        assertThat(queryResult.getDisplayText()).isEqualTo("text1");
        assertThat(queryResult.getHandleUri()).isEqualTo("url1");

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testGetResultsWithTwoResults() throws InterruptedException {
        Object [] results = { RESULT_ENTRY1, RESULT_ENTRY2 };
        generator = new RubyQueryResultGenerator(results);

        Iterator<QueryResult> iterator = generator.getResults().iterator();
        assertThat(iterator.hasNext()).isTrue();

        QueryResult queryResult = iterator.next();
        assertThat(queryResult.getDisplayIcon()).isEqualTo("icon1");
        assertThat(queryResult.getDisplayText()).isEqualTo("text1");
        assertThat(queryResult.getHandleUri()).isEqualTo("url1");


        assertThat(iterator.hasNext()).isTrue();

        queryResult = iterator.next();
        assertThat(queryResult.getDisplayIcon()).isEqualTo("icon2");
        assertThat(queryResult.getDisplayText()).isEqualTo("text2");
        assertThat(queryResult.getHandleUri()).isEqualTo("url2");

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetResultsWithEmptyResult() throws InterruptedException {
        Object [] results = { };
        generator = new RubyQueryResultGenerator(results);

        Iterator<QueryResult> iterator = generator.getResults().iterator();
        assertThat(iterator.hasNext()).isFalse();

        iterator.next();
    }

}

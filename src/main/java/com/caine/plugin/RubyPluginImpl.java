package com.caine.plugin;

import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.caine.ui.FileSearchPlugin;
import com.caine.ui.SearchController;
import org.jruby.RubyArray;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Ruby plugin implementation.
 */
public class RubyPluginImpl extends ThreadPluginImpl {

    // TODO: changed to RubyObject class and use reflection for calling search()
    FileSearchPlugin plugin;

    public RubyPluginImpl(SearchController searchController, Class rubyClassType) throws IllegalAccessException,
            InstantiationException {
        super(searchController);
        plugin = (FileSearchPlugin) rubyClassType.newInstance();
    }

    // TODO: changed to incrementally send results to UI
    @Override
    protected void performQuery(String queryString) {

        if (queryString.length() == 0) {
            return;
        }

        RubyArray results = (RubyArray) plugin.search(queryString);
        QueryResultGenerator queryResults = new RubyQueryResultGenerator(results);
        searchController.appendSearchResult(queryString, queryResults);
    }

    @Override
    public void cancelQuery() {
        throw new UnsupportedOperationException();
    }

    class RubyQueryResultGenerator implements QueryResultGenerator {

        private final RubyArray results;

        RubyQueryResultGenerator(RubyArray results) {
            this.results = results;
        }

        @Override
        public Iterable<QueryResult> getResults() {

            return Stream.generate(createQueryResultSupplier())
                         .limit(results.size())
                         ::iterator;
        }

        private Supplier<QueryResult> createQueryResultSupplier() {

            return new Supplier<QueryResult>() {

                int index = 0;

                @Override
                public QueryResult get() {
                    RubyArray item = (RubyArray) results.get(index++);
                    return QueryResult.builder()
                            .displayIcon((String) item.get(0))
                            .displayText((String) item.get(1))
                            .handleUri((String) item.get(2))
                            .build();
                }
            };
        }
    }
}

package com.caine.plugin;

import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;
import com.caine.ui.FileSearchPlugin;
import com.caine.ui.SearchController;
import com.google.common.base.Suppliers;
import org.jruby.RubyArray;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RubyPlugin extends ThreadBasePlugin {
    FileSearchPlugin plugin;

    public RubyPlugin(SearchController searchController, Class rubyClassType) throws IllegalAccessException,
            InstantiationException {
        super(searchController);
        plugin = (FileSearchPlugin) rubyClassType.newInstance();
    }

    @Override
    protected void performQuery(String queryString) {
        if (queryString.length() == 0) {
            System.out.println("return with nothing");
            return;
        }

        RubyArray results = (RubyArray) plugin.search(queryString);

        System.out.printf("Size of result: %d\n", results.size());
        searchController.appendSearchResult(queryString, new RubyResultGenerator(results));
    }

    class RubyResultGenerator implements QueryResultGenerator {
        private final RubyArray results;

        RubyResultGenerator(RubyArray results) {
            this.results = results;
        }

        @Override
        public Iterable<QueryResult> getResults() {
            return () -> {
                return Stream.generate(new Supplier<QueryResult>() {
                    int i = 0;

                    @Override
                    public QueryResult get() {
                        RubyArray item = (RubyArray) results.get(i++);
                        return QueryResult.builder()
                                .displayIcon((String) item.get(0))
                                .displayText((String) item.get(1))
                                .handleUri((String) item.get(2))
                                .build();
                    }
                }).limit(results.size()).iterator();
            };
        }
    }
}

package com.caine.plugin;

import com.caine.core.QueryResult;
import com.caine.core.QueryResultGenerator;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Generator of QueryResult from string array.
 */
class RubyQueryResultGenerator implements QueryResultGenerator {

    private final Object[] resultEntries;

    RubyQueryResultGenerator(Object[] results) {
        this.resultEntries = results;
    }

    @Override
    public Iterable<QueryResult> getResults() {

        return Stream.generate(createQueryResultSupplier())
                .limit(resultEntries.length)
                ::iterator;
    }

    private Supplier<QueryResult> createQueryResultSupplier() {

        return new Supplier<QueryResult>() {

            int index = 0;

            @Override
            public QueryResult get() {
                String[] resultEntry = (String []) resultEntries[index++];
                return QueryResult.builder()
                        .displayIcon(resultEntry[0])
                        .displayText(resultEntry[1])
                        .handleUri(resultEntry[2])
                        .build();
            }
        };
    }
}
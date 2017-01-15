package com.caine.plugin;

import com.caine.ui.FileSearchPlugin;
import com.caine.ui.SearchController;
import org.jruby.RubyArray;

import java.util.LinkedList;
import java.util.List;

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

        List<String> strings = callPluginSearch(queryString);

        System.out.printf("Size of result: %d\n", strings.size());
        searchController.appendSearchResult(queryString, strings);
    }

    private List<String> callPluginSearch(String queryString) {
        RubyArray results = (RubyArray) plugin.search(queryString);

        List<String> strings = new LinkedList<>();
        for (Object o : results) {
            strings.add((String) o);
        }
        return strings;
    }
}

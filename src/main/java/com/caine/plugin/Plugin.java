package com.caine.plugin;

public interface Plugin {

    String getName();
    Object[] queryByPage(String queryString, int pageNumber);
    boolean hasMorePage(int PageNumber);
}

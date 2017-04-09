package com.caine.plugin;

public interface Plugin {

    void load(String instanceName);

    Object[] queryByPage(String queryString, int pageNumber);
    boolean hasMorePage(int PageNumber);
}

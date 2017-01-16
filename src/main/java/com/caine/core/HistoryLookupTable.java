package com.caine.core;

import com.google.inject.Singleton;

import java.util.HashMap;

/**
 *
 */
@Singleton
public class HistoryLookupTable {
    HashMap<String, Long> temperatoryHistoryTable = new HashMap<>(100);

    public long getLastAccessDate(String item) {
        Long date = temperatoryHistoryTable.get(item);
        if (date == null) {
            return 0;
        }
        return date;
    }

    public void access(String item) {
        temperatoryHistoryTable.put(item, System.currentTimeMillis());
    }
}

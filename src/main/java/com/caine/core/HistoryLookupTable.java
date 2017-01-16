package com.caine.core;

import com.google.inject.Singleton;
import com.sleepycat.je.*;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Stores file access history in file based database.
 */
@Singleton
public class HistoryLookupTable implements Serializable {
    private static final String DATABASE_FOLDER_NAME = System.getProperty("user.home") + "/.config/caine/data";
    private static final String FSODB_FILENAME = "history.db";

    private HashMap<String, Long> history = new HashMap<>(100);

    private EnvironmentConfig environmentConfig = new EnvironmentConfig();
    private DatabaseConfig config = new DatabaseConfig();
    private Environment environment;
    private Database database;

    public HistoryLookupTable() {
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);
        config.setAllowCreate(true);
        config.setTransactional(true);

        environment = new Environment(new File(DATABASE_FOLDER_NAME), environmentConfig);
        database = environment.openDatabase(null, FSODB_FILENAME, config);
    }

    public long getLastAccessDate(String item) {
        DatabaseEntry keyEntry = new DatabaseEntry(item.getBytes());
        DatabaseEntry dataEntry = new DatabaseEntry();

        if (database.get(null, keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            String string = new String(dataEntry.getData());
            long value = Long.parseLong(string);
            return value;
        }
        return 0;
    }

    public void access(String item) {
        try {
            DatabaseEntry keyEntry = new DatabaseEntry(item.getBytes("UTF-8"));

            Long now = System.currentTimeMillis();
            DatabaseEntry dataEntry = new DatabaseEntry(String.valueOf(now).getBytes());

            database.put(null, keyEntry, dataEntry);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void shutdown() {
        database.close();
        environment.close();
    }
}

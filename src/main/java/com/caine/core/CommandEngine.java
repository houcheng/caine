package com.caine.core;

import com.caine.exception.NoSuchCommandException;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parses and executes command.
 */
public class CommandEngine {

    public static final String CAINE_BIN_PATH = "/opt/caine/bin/caine";

    public boolean isCommandString(String queryString) {
        return queryString.startsWith(":");
    }

    public void executeCommand(String queryString) {

        Preconditions.checkArgument(queryString.startsWith(":"), "commands should start with :");

        List<String> commands = Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(queryString.substring(1));

        if (commands.size() == 0) {
            return;
        }

        String token = commands.get(0).toLowerCase();
        if (token.equals("stop")) {
            doStopCommand();
        } else if (token.equals("restart")) {
            doRestartCommand();
        } else if (token.equals("do-nothing")) {
            doNothingForUnitTest();
        } else {
            String message = String.format("Unrecognized command: %s\n", token);
            throw new NoSuchCommandException(message);
        }
    }

    private void doNothingForUnitTest() {

    }

    private void doRestartCommand() {

        final ArrayList<String> command = new ArrayList<String>();
        command.add(CAINE_BIN_PATH);

        tryToStartProcess(command);

        System.exit(0);
    }

    private void tryToStartProcess(ArrayList<String> command) {

        final ProcessBuilder builder = new ProcessBuilder(command);

        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doStopCommand() {
        System.exit(0);
    }
}

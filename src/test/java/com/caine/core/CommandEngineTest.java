package com.caine.core;

import com.caine.exception.NoSuchCommandException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandEngineTest {

    private CommandEngine target = new CommandEngine();

    @Test
    public void isCommandString() {

        assertThat(target.isCommandString(":")).isTrue();
        assertThat(target.isCommandString(": ok")).isTrue();
        assertThat(target.isCommandString(":ok")).isTrue();
        assertThat(target.isCommandString(":o")).isTrue();

        assertThat(target.isCommandString(": ok ok2")).isTrue();
    }

    @Test(expected = NoSuchCommandException.class)
    public void testExecuteCommandThrowsException() {
        target.executeCommand(":nocommand");
    }

    @Test
    public void testExecuteCommandForEmptyCommand() {
        target.executeCommand(":");
    }

    @Test
    public void testExecuteCommandForOneSpaceCommand() {
        target.executeCommand(": ");
    }

    @Test
    public void testExecuteCommandForTwoSpaceCommand() {
        target.executeCommand(":  ");
    }

    @Test
    public void testExecuteCommandStopCommand() {
        target.executeCommand(": do-nothing");
    }

}
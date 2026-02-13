package command.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpCommandTest {

    @Test
    void testExecuteHelpCommand() {
        HelpCommand helpCommand = new HelpCommand();

        String result = helpCommand.execute();

        assertNotNull(result, "Help message can't be null");

        assertTrue(result.contains("Available Commands:"), "Result should have titles");
        assertTrue(result.contains("[Public]"), "Result should have a public section");
        assertTrue(result.contains("[Authenticated]"), "Result should have authenticated section");

        assertTrue(result.contains("register"), "Help should explain register command");
        assertTrue(result.contains("login"), "Help should explain login command");
        assertTrue(result.contains("deposit"), "Help should explain deposit command");
        assertTrue(result.contains("buy"), "Help should explain buy command");
        assertTrue(result.contains("get-wallet-summary"), "Help should explain summary command");
    }

}

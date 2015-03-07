/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Flaiker on 29.11.2014.
 */
public class ConsoleManager {
    private List<ConsoleCommand> commandList = new ArrayList<>();

    public void addCommand(ConsoleCommand command) {
        commandList.add(command);
    }

    public void addCommands(List<ConsoleCommand> consoleCommandList) {
        commandList.addAll(consoleCommandList);
    }

    public void clearCommands() {
        commandList.clear();
    }

    public void submitConsoleString(String commandString) {
        ConsoleCommand foundCommand = null;
        String normalizedString = commandString.trim().replaceAll("\\s+", " ");
        String commandName = normalizedString.split(" ")[0];

        for (ConsoleCommand command : commandList) {
            if (command.commandName.equals(commandName)) foundCommand = command;
        }

        if (foundCommand == null) return;

        HashMap<String, String> parValuePairs = new HashMap<>();
        if (commandString.trim().length() - commandName.length() + 1 > 2) {
            String parString = normalizedString.substring(commandName.length() + 1);
            String[] parStrings = parString.split(" ");
            for (String pair : parStrings) {
                String[] parAndValue = pair.split(":");
                if (parAndValue.length == 2) {
                    String par = parAndValue[0];
                    String val = parAndValue[1];
                    parValuePairs.put(par, val);
                } else parValuePairs.put(pair, "");
            }
        }
        foundCommand.executeMethod.OnCommandFired(parValuePairs);
    }

    public static class ConsoleCommand {
        private String          commandName;
        private CommandExecutor executeMethod;

        public ConsoleCommand(String command, CommandExecutor executeMethod) {
            this.commandName = command;
            this.executeMethod = executeMethod;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    public interface CommandExecutor {
        public void OnCommandFired(HashMap<String, String> parValuePairs);
    }

    public interface CommandableInstance {
        public List<ConsoleCommand> getConsoleCommands();
    }
}

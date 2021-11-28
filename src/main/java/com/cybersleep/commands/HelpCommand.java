package com.cybersleep.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.cybersleep.messagesender.MessageSender;


public class HelpCommand implements CommandExecutor
{
    private final String helpMessage;
    private final MessageSender messageSender;

    public HelpCommand(String helpMessage)
    {
        this.helpMessage = helpMessage;
        this.messageSender = new MessageSender();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        this.messageSender.sendMessage(sender, this.helpMessage);

        return true;
    }
}

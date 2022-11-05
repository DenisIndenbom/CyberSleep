package com.cybersleep.utils;

import org.bukkit.command.CommandSender;

public class MessageSender
{
    private final FormatText formatText = new FormatText();

    public void sendMessage(CommandSender sender, String message)
    {
        String newMessage = this.formatText.format(message);
        try
        {
            sender.sendMessage(newMessage);
        }
        catch (Exception ignored)
        {
            sender.getServer().getLogger().info(newMessage);
        }
    }

    public void sendMessage(CommandSender sender, String message, String replaceable, String replacement)
    {
        this.sendMessage(sender, formatText.format(message, replaceable, replacement));
    }
}

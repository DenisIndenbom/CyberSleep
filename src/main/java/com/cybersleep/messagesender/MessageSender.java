package com.cybersleep.messagesender;

import org.bukkit.command.CommandSender;

import com.cybersleep.formattext.FormatText;

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
}

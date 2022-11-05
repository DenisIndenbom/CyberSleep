package com.cybersleep.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.cybersleep.CyberSleep;
import com.cybersleep.utils.MessageSender;

import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor
{
    private final CyberSleep plugin;
    private final String reloadMessage;

    private final MessageSender messageSender = new MessageSender();

    public ReloadCommand(String reloadMessage, CyberSleep plugin)
    {
        this.reloadMessage = reloadMessage;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        if (!sender.isOp())
        {
            this.messageSender.sendMessage(sender, ChatColor.RED + "<c5>[CyberSleepPlugin] <c4>Sorry, you don't have permissions to reload plugin!");
            return false;
        }

        boolean result = plugin.reloadPlugin();

        if (result) this.messageSender.sendMessage(sender, this.reloadMessage);
        else this.messageSender.sendMessage(sender, "<c5>[CyberSleepPlugin] <c4>Error: The plugin reload failed. Perhaps config.yml has syntax errors!");

        return true;
    }
}

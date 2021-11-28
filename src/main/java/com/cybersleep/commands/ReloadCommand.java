package com.cybersleep.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.cybersleep.CyberSleepPlugin;
import com.cybersleep.messagesender.MessageSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor
{
    private final String pluginName;
    private final String reloadMessage;

    private final MessageSender messageSender = new MessageSender();

    public ReloadCommand(String pluginName, String reloadMessage)
    {
        this.pluginName = pluginName;
        this.reloadMessage = reloadMessage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!sender.isOp())
        {
            this.messageSender.sendMessage(sender,
                                           "[" + this.pluginName + "]" + "Sorry, you don't have permissions to reload plugin!");
            return false;
        }

        CyberSleepPlugin plugin = (CyberSleepPlugin) sender.getServer().getPluginManager().getPlugin(this.pluginName);
        if (plugin == null) return false;

        boolean result  = plugin.reloadPlugin();

        if (result) this.messageSender.sendMessage(sender, this.reloadMessage);
        else this.messageSender.sendMessage(sender, "Error: The plugin reload failed. Perhaps config.yml has syntax errors!");

        return true;
    }
}

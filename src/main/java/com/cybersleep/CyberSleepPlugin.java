package com.cybersleep;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.HandlerList;

import com.cybersleep.listeners.PlayerListener;
import com.cybersleep.commands.HelpCommand;
import com.cybersleep.commands.ReloadCommand;

import java.util.Objects;


public class CyberSleepPlugin extends JavaPlugin
{
    private PlayerListener playerListener;

    @Override
    public void onLoad()
    {saveDefaultConfig();}

    @Override
    public void onEnable()
    {
        try
        {
            loadPlugin();
            getLogger().info("CyberSleep is enable!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            getLogger().warning("An error occurred when launching the plugin! The plugin is not running! Please check for config.yml syntax errors!");
        }
    }

    @Override
    public void onDisable()
    {
        this.playerListener.disable();

        HandlerList.unregisterAll();
    }

    public boolean reloadPlugin()
    {
        try
        {
            // reload config
            reloadConfig();
            // disable listeners
            this.playerListener.disable();
            // unregister all
            HandlerList.unregisterAll();
            // load plugin
            loadPlugin();
        }
        catch (Exception ignored){return false;}

        return true;
    }

    public void loadPlugin()
    {
        // registering commands in the handler
        Objects.requireNonNull(getCommand("help")).setExecutor(
                new HelpCommand(getConfig().getString("messages.helpMessage")));
        Objects.requireNonNull(getCommand("reload")).setExecutor(
                new ReloadCommand(getConfig().getString("messages.reloadMessage"), this));

        // initialize listeners
        this.playerListener = new PlayerListener(getConfig().getString("messages.enterBedMessage"),
                                                 getConfig().getString("messages.exitBedMessage"),
                                                 getConfig().getString("sleeping_bar.title"),
                                                 Objects.requireNonNull(getConfig().getString("sleeping_bar.color")),
                                                 getConfig().getDouble("max_percent_of_sleeping_players"), this);

        // registering listeners in the handler
        getServer().getPluginManager().registerEvents(this.playerListener, this);
    }
}
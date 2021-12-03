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
        this.disablePlugin();
    }

    public boolean reloadPlugin()
    {
        try
        {
            // reload config
            this.reloadConfig();
            // disable plugin
            this.disablePlugin();
            // load plugin
            this.loadPlugin();
        }
        catch (Exception ignored)
        {return false;}

        return true;
    }

    public void loadPlugin()
    {
        // registering commands in the handler
        Objects.requireNonNull(this.getCommand("about_cybersleep")).setExecutor(
                new HelpCommand(this.getConfig().getString("messages.helpMessage")));
        Objects.requireNonNull(this.getCommand("reload_cybersleep")).setExecutor(
                new ReloadCommand(this.getConfig().getString("messages.reloadMessage"), this));

        // initialize listeners
        this.playerListener = new PlayerListener(this.getConfig().getString("messages.enterBedMessage"),
                                                 this.getConfig().getString("messages.exitBedMessage"),
                                                 this.getConfig().getString("sleeping_bar.title"),
                                                 Objects.requireNonNull(this.getConfig().getString("sleeping_bar.color")),
                                                 this.getConfig().getDouble("max_percent_of_sleeping_players"), this);

        // registering listeners in the handler
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
    }

    private void disablePlugin()
    {
        // disable listeners
        this.playerListener.disable();
        HandlerList.unregisterAll(this.playerListener);
        this.playerListener = null;
    }
}
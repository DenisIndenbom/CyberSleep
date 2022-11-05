package com.cybersleep;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

import com.cybersleep.listeners.PlayerListener;
import com.cybersleep.commands.HelpCommand;
import com.cybersleep.commands.ReloadCommand;
import com.cybersleep.SleepManagement.SleepManager;

import java.util.Objects;


public class CyberSleep extends JavaPlugin
{
    private SleepManager sleepManager;
    private PlayerListener playerListener;

    @Override
    public void onLoad()
    {this.saveDefaultConfig();}

    @Override
    public void onEnable()
    {
        try
        {
            this.loadPlugin();
            this.getLogger().info("CyberSleep is enable!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.getLogger().warning("An error occurred when launching the plugin! The plugin is not running! Please check for config.yml syntax errors!");
        }
    }

    @Override
    public void onDisable()
    {this.disablePlugin();}

    public void loadPlugin()
    {
        // registering commands in the handler
        Objects.requireNonNull(this.getCommand("about_cybersleep")).
                setExecutor(new HelpCommand(this.getConfig().getString("messages.help_message")));
        Objects.requireNonNull(this.getCommand("reload_cybersleep")).
                setExecutor(new ReloadCommand(this.getConfig().getString("messages.reload_message"), this));

        // initialize sleep manager
        this.sleepManager = new SleepManager(this, this.getConfig());
        // set count of online players
        this.sleepManager.setPlayers(this.getServer().getOnlinePlayers().size());
        // count sleeping players
        long sleepers = 0;
        for (Player p : this.getServer().getOnlinePlayers()) {sleepers += (p.isSleeping()) ? 1 : 0;}
        // set count of sleeping players
        this.sleepManager.setSleepers(sleepers);

        // initialize listeners
        this.playerListener = new PlayerListener(sleepManager, getConfig());

        // registering listeners in the handler
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
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

    private void disablePlugin()
    {
        // disable sleep manager
        this.sleepManager.disable();

        // disable listeners
        HandlerList.unregisterAll(this.playerListener);
        this.playerListener = null;
    }
}
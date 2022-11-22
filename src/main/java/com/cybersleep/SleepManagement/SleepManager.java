package com.cybersleep.SleepManagement;

import com.cybersleep.utils.FormatText;

import com.cybersleep.CyberSleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SleepManager
{
    private final CyberSleep plugin;
    private final long morning;
    private final long night;
    private final long skipDelay;
    private final long delayHiding;
    private final double skipPercentage;
    private final String barTitle;
    private final BossBar sleepingBar;

    private long sleepers = 0;
    private long players = 0;

    private boolean skippingNight = false;

    public SleepManager(CyberSleep plugin, FileConfiguration config)
    {
        // set plugin
        this.plugin = plugin;

        // set constants
        this.skipPercentage = config.getDouble("skip_night.players_sleeping_percentage");
        this.skipDelay = config.getLong("skip_night.delay");
        this.delayHiding = config.getLong("sleeping_bar.delay_hiding");
        this.morning = config.getLong("time.morning");
        this.night = config.getLong("time.night");

        // create sleeping bar
        this.barTitle = new FormatText().format(config.getString("sleeping_bar.title"));

        BarColor barColor;
        switch (Objects.requireNonNull(config.getString("sleeping_bar.color")))
        {
            case "blue" -> barColor = BarColor.BLUE;
            case "red" -> barColor = BarColor.RED;
            case "yellow" -> barColor = BarColor.YELLOW;
            case "green" -> barColor = BarColor.GREEN;
            case "pink" -> barColor = BarColor.PINK;
            case "purple" -> barColor = BarColor.PURPLE;
            default -> barColor = BarColor.WHITE;
        }

        this.sleepingBar = Bukkit.createBossBar(this.barTitle, barColor, BarStyle.SOLID);
    }

    public void disable()
    {this.sleepingBar.removeAll();}

    public void addSleeper() {this.sleepers++;}
    public void subSleeper() {this.sleepers -= (this.sleepers > 0) ? 1 : 0;}

    public void setPlayers(long players) {this.players = players;}
    public void setSleepers(long sleepers) {this.sleepers = sleepers;}

    public void updateSleepingBar()
    {
        // calculate max sleepers
        long maxSleepers = (long) Math.ceil(((double) this.players * this.skipPercentage));
        maxSleepers = Math.max(maxSleepers, 1);

        // calculate bar progress
        double progress = (double) this.sleepers / maxSleepers;
        progress = Math.max(Math.min(progress, 1.0), 0);

        // update sleeping bar
        this.sleepingBar.setProgress(progress);
        this.sleepingBar.setTitle(this.barTitle + " " + this.sleepers + "/" + maxSleepers);
    }

    public boolean isZeroSleepers()
    {return this.sleepers == 0;}

    public boolean isShowSleepingBar()
    {return this.sleepingBar.getPlayers().size() > 0;}

    public boolean isValidTimeToSleep(@NotNull World world)
    {return world.getTime() >= this.night || world.isThundering();}

    public boolean isValidSkippingNight(World world)
    {return ((double) this.sleepers / this.players) >= this.skipPercentage && isValidTimeToSleep(world);}

    public void skipNight(World world)
    {
        if (this.skippingNight) return;

        this.skippingNight = true;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                skippingNight = false;
                if (!isValidSkippingNight(world)) return;

                // skip night or storm
                if (world.isThundering()) world.setStorm(false);
                world.setTime(morning);

                // hide sleeping bar
                sleepingBar.removeAll();
            }

        }.runTaskLater(this.plugin, this.skipDelay);
    }

    public void showSleepingBar(@NotNull List<Player> players)
    {for (Player p : players) this.sleepingBar.addPlayer(p);}

    public void showSleepingBar(@NotNull Player player)
    {this.sleepingBar.addPlayer(player);}

    public void hideSleepingBar()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {if (sleepers == 0) sleepingBar.removeAll();}

        }.runTaskLater(this.plugin, this.delayHiding);
    }

}

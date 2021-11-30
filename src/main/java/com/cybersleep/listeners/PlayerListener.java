package com.cybersleep.listeners;

import com.cybersleep.CyberSleepPlugin;
import com.cybersleep.formattext.FormatText;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener
{
    private final CyberSleepPlugin plugin;

    private final String enterBedMessage;
    private final String exitBedMessage;

    private final long morning = 0;
    private final long night = 13000;

    private final double percentagePlayersToSkipNight;
    private long sleepingPlayersNum = 0;
    private long maxSleepingPlayers = 0;

    private boolean skippingNight = false;

    private final BarColor sleepingBarColor;
    private final String sleepingBarTitle;
    private final BossBar sleepingBar;

    private final FormatText formatText = new FormatText();

    public PlayerListener(String enterBedMessage, String exitBedMessage, String sleepingBarTitle,
                          String sleepingBarColor, double percentagePlayersToSkipNight, CyberSleepPlugin plugin)
    {
        this.enterBedMessage = enterBedMessage;
        this.exitBedMessage = exitBedMessage;

        this.sleepingBarTitle = formatText.format(sleepingBarTitle);
        this.percentagePlayersToSkipNight = percentagePlayersToSkipNight;

        switch (sleepingBarColor)
        {
            case "blue" -> this.sleepingBarColor = BarColor.BLUE;
            case "red" -> this.sleepingBarColor = BarColor.RED;
            case "yellow" -> this.sleepingBarColor = BarColor.YELLOW;
            case "green" -> this.sleepingBarColor = BarColor.GREEN;
            case "pink" -> this.sleepingBarColor = BarColor.PINK;
            case "purple" -> this.sleepingBarColor = BarColor.PURPLE;
            default -> this.sleepingBarColor = BarColor.WHITE;
        }

        // create sleepingBar
        this.sleepingBar = Bukkit.createBossBar(this.sleepingBarTitle, this.sleepingBarColor, BarStyle.SOLID);

        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event)
    {
        // calculate the number of sleeping players needed to skip the night
        this.maxSleepingPlayers = (int) ((event.getPlayer().getWorld().getPlayers().size() + 1) * this.percentagePlayersToSkipNight);
        if (this.maxSleepingPlayers <= 0) this.maxSleepingPlayers = 1;
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event)
    {
        // calculate the number of sleeping players needed to skip the night
        this.maxSleepingPlayers = (int) ((event.getPlayer().getWorld().getPlayers().size() - 1) * this.percentagePlayersToSkipNight);
        if (this.maxSleepingPlayers <= 0) this.maxSleepingPlayers = 1;
    }

    @EventHandler
    public void onPlayerEnterBed(@NotNull PlayerBedEnterEvent event)
    {
        // return void if event is cancelled
        if (event.isCancelled()) return;

        // take the player's class
        Player player = event.getPlayer();
        // take world
        World world = player.getWorld();
        // send message
        this.sendMessage(player, this.enterBedMessage, player.getName(), "<playerName>");
        // add players to display sleeping bar
        if (sleepingBar.getPlayers().size() == 0 && world.getTime() >= this.night)
            for (Player p : world.getPlayers()) this.sleepingBar.addPlayer(p);

        if (this.maxSleepingPlayers <= 0) this.updateMaxSleepingPlayers(world.getPlayers().size());

        // add sleeping player
        this.sleepingPlayersNum += 1;

        this.updateSleepingBar();

        if (this.maxSleepingPlayers <= this.sleepingPlayersNum && !this.skippingNight)
        {
            this.skippingNight = true;

            this.removeSleepingBarWithDelay(30);

            this.skipNight(world);
        }
    }

    @EventHandler
    public void onPlayerExitBed(@NotNull PlayerBedLeaveEvent event)
    {
        // return void if event is cancelled
        if (event.isCancelled()) return;

        // take player`s class
        Player player = event.getPlayer();
        // take world
        World world = player.getWorld();

        // sub sleeping player
        if (this.sleepingPlayersNum > 0) this.sleepingPlayersNum -= 1;

        if (this.maxSleepingPlayers <= 0) this.updateMaxSleepingPlayers(world.getPlayers().size());

        if ((world.getTime() >= this.morning) && (world.getTime() <= this.night))
        {
            this.sendMessage(player, this.exitBedMessage, player.getName(), "<playerName>");
        }
        else if (this.sleepingPlayersNum == 0 && !this.skippingNight) this.removeSleepingBarWithDelay(200);

        // update sleeping bar progress
        this.updateSleepingBar();
    }

    public void disable()
    {
        if (this.sleepingBar != null) this.sleepingBar.removeAll();
    }

    private void updateMaxSleepingPlayers(int playersNum)
    {
        // calculate the number of sleeping players to skip the night
        this.maxSleepingPlayers = (int) (playersNum * this.percentagePlayersToSkipNight);
        if (this.maxSleepingPlayers <= 0) this.maxSleepingPlayers = 1;
    }

    private void skipNight(World world)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                world.setTime(morning);
                skippingNight = false;
            }
        }.runTaskLater(plugin, 100);
    }

    private void removeSleepingBarWithDelay(long delay)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (sleepingPlayersNum == 0) sleepingBar.removeAll();
            }
        }.runTaskLater(plugin, delay);
    }

    private void updateSleepingBar()
    {
        try
        {
            // set sleeping bar progress
            this.sleepingBar.setProgress((double) this.sleepingPlayersNum / this.maxSleepingPlayers);
            this.sleepingBar.setTitle(this.sleepingBarTitle + " " + sleepingPlayersNum + "/" + this.maxSleepingPlayers);
        }
        catch (Exception ignored)
        {
        }
    }

    private void sendMessage(Player recipientPlayer, String text)
    {
        recipientPlayer.sendMessage(this.formatText.format(text));
    }

    private void sendMessage(Player recipientPlayer, String text, String var, String replacement)
    {
        recipientPlayer.sendMessage(this.formatText.format(text, var, replacement));
    }
}

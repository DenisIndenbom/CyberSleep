package com.cybersleep.listeners;

import com.cybersleep.SleepManagement.SleepManager;
import com.cybersleep.utils.MessageSender;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;


public class PlayerListener implements Listener
{
    private final SleepManager sleepManager;
    private final MessageSender messageSender = new MessageSender();

    private final String enterBedMessage;
    private final String exitBedMessage;

    public PlayerListener(SleepManager sleepManager, FileConfiguration config)
    {
        this.sleepManager = sleepManager;

        this.enterBedMessage = config.getString("messages.enter_bed_message");
        this.exitBedMessage = config.getString("messages.exit_bed_message");
    }

    @EventHandler
    public void onPLayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();

        this.sleepManager.setPlayers(player.getWorld().getPlayers().size() + 1);
        this.sleepManager.updateSleepingBar();

        if (this.sleepManager.isShowSleepingBar() && this.sleepManager.isValidTimeToSleep(player.getWorld()))
            this.sleepManager.showSleepingBar(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        this.sleepManager.setPlayers(event.getPlayer().getWorld().getPlayers().size() - 1);
        this.sleepManager.updateSleepingBar();
    }

    @EventHandler
    public void onPlayerEnterBed(@NotNull PlayerBedEnterEvent event)
    {
        // return void if event is cancelled
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        World world = player.getWorld();

        if (this.sleepManager.isZeroSleepers() || (!this.sleepManager.isZeroSleepers() && !this.sleepManager.isShowSleepingBar()))
            this.sleepManager.showSleepingBar(world.getPlayers());

        this.sleepManager.addSleeper();

        if (this.sleepManager.isValidSkippingNight(world)) this.sleepManager.skipNight(world);

        this.sleepManager.updateSleepingBar();

        // send a message if it's night
        if (this.sleepManager.isValidTimeToSleep(world))
            this.messageSender.sendMessage(player, this.enterBedMessage, "<playerName>", player.getName());
    }

    @EventHandler
    public void onPlayerExitBed(@NotNull PlayerBedLeaveEvent event)
    {
        // return void if event is cancelled
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        World world = player.getWorld();

        this.sleepManager.subSleeper();

        if (this.sleepManager.isZeroSleepers()) this.sleepManager.hideSleepingBar();

        this.sleepManager.updateSleepingBar();

        // send a message if it's morning
        if (!this.sleepManager.isValidTimeToSleep(world))
            this.messageSender.sendMessage(player, this.exitBedMessage, "<playerName>", player.getName());
    }
}

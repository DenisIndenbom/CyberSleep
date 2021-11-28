package com.cybersleep.listeners;

import org.bukkit.Bukkit;
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

import com.cybersleep.formattext.FormatText;

import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener
{
    private final String enterBedMessage;
    private final String exitBedMessage;

    private final long morning = 0;
    private final long night = 13000;

    private final double percentagePlayersToSkipNight;
    private long sleepingPlayersNum = 0;
    private long maxSleepingPlayers = 0;

    private final BarColor sleepingBarColor;
    private final String sleepingBarTitle;
    private final BossBar sleepingBar;

    final FormatText formatText = new FormatText();

    public PlayerListener(String enterBedMessage, String exitBedMessage, String sleepingBarTitle,
                          String sleepingBarColor, double percentagePlayersToSkipNight)
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
        this.sleepingBar = Bukkit.createBossBar("Sleeping", this.sleepingBarColor, BarStyle.SOLID);
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
        // send message
        sendMessage(player, this.enterBedMessage, player.getName(), "<playerName>");
        // add players to display sleeping bar
        if (sleepingBar.getPlayers().size() == 0)
            for (Player p : player.getWorld().getPlayers()) this.sleepingBar.addPlayer(p);

        if (this.maxSleepingPlayers <= 0) updateMaxSleepingPlayers(player.getWorld().getPlayers().size());

        // add sleeping player
        this.sleepingPlayersNum += 1;

        updateSleepingBar();

        if (this.maxSleepingPlayers <= this.sleepingPlayersNum)
        {
            skipNight(player.getWorld());
        }
    }

    @EventHandler
    public void onPlayerExitBed(@NotNull PlayerBedLeaveEvent event)
    {
        // return void if event is cancelled
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        // sub sleeping player
        if (this.sleepingPlayersNum > 0) this.sleepingPlayersNum -= 1;

        if (this.maxSleepingPlayers <= 0) updateMaxSleepingPlayers(player.getWorld().getPlayers().size());

        if ((player.getWorld().getTime() <= this.night) && (player.getWorld().getTime() >= this.morning))
        {
            if (sleepingPlayersNum == 0) this.sleepingBar.removeAll();
            sendMessage(player, this.exitBedMessage, player.getName(), "<playerName>");
        }

        // update sleeping bar progress
        updateSleepingBar();
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

    private void skipNight(@NotNull World world)
    {
        world.setTime(this.morning);
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
        recipientPlayer.sendMessage(formatText.format(text));
    }

    private void sendMessage(Player recipientPlayer, String text, String var, String replacedWord)
    {
        recipientPlayer.sendMessage(formatText.format(text, var, replacedWord));
    }
}

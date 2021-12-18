package com.programmer5000.rompplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Random;

public class StatsBoard {
  private Scoreboard board = null;
  private Statistic trackedStatistic;
  private FullySpecifiedStatistic.CustomStatistic customStatistic;
  private Objective objective;
  private Objective deathsObjective;
  private Objective healthObjective;
  private FullySpecifiedStatistic fullStat = null;
  private Object statEntityOrMaterialOrNull = null;

  StatsBoard(){
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null;
    this.board = manager.getNewScoreboard();
    StaticScoreboardManager.getInstance().addToBoard(this.board);
    this.trackedStatistic = null;
    this.statEntityOrMaterialOrNull = null;
    this.fullStat = null;

    init();
  }

  StatsBoard(Statistic trackedStatistic, Object statEntityOrMaterialOrNull) {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null;
    this.board = manager.getNewScoreboard();
    StaticScoreboardManager.getInstance().addToBoard(this.board);
    this.trackedStatistic = trackedStatistic;
    this.statEntityOrMaterialOrNull = statEntityOrMaterialOrNull;
    this.fullStat = new FullySpecifiedStatistic(trackedStatistic, statEntityOrMaterialOrNull);

    init();
  }

  StatsBoard(FullySpecifiedStatistic.CustomStatistic customStatistic) {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null;
    this.board = manager.getNewScoreboard();
    StaticScoreboardManager.getInstance().addToBoard(this.board);
    this.customStatistic = customStatistic;
    this.statEntityOrMaterialOrNull = null;
    this.fullStat = new FullySpecifiedStatistic(customStatistic);

    init();
  }

  @Deprecated
  public static void clearPlayer(@org.jetbrains.annotations.NotNull Player player) {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null; // should not be null

    PlayerDataManager.setScoreboard(player, null);
    Scoreboard newScoreboard = manager.getNewScoreboard();
    StaticScoreboardManager.getInstance().addToBoard(newScoreboard);
    player.setScoreboard(newScoreboard);
  }

  private void init() {
    Random rand = new Random();
    String objectiveKey = String.valueOf(rand.nextInt(Integer.MAX_VALUE));

    String objectiveName = getObjectiveName();
    //    logger.info("Creating new board & registering new objective with key " + objectiveKey + " and name " + objectiveName);
    if(objectiveName != null) {
      this.objective = board.registerNewObjective(objectiveKey, "dummy", objectiveName);
      this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    this.deathsObjective = board.registerNewObjective(String.valueOf(rand.nextInt(Integer.MAX_VALUE)), "dummy", "Deaths");
    this.deathsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

    this.healthObjective = board.registerNewObjective(String.valueOf(rand.nextInt(Integer.MAX_VALUE)), "dummy", "Health");
    this.healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
  }

  public boolean updateScoresForAllPlayers() {
    boolean positiveValues = false;
    OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();

    for (OfflinePlayer pastPlayer : allPlayers) {
      String name = pastPlayer.getName();
      if (name == null) {
        name = pastPlayer.getUniqueId().toString();
      }

      if(this.objective != null) {
        int scoreValue = 0;

        if (this.trackedStatistic != null) {
          if (this.statEntityOrMaterialOrNull instanceof Material) {
            scoreValue = pastPlayer.getStatistic(this.trackedStatistic, (Material) statEntityOrMaterialOrNull);
          } else if (statEntityOrMaterialOrNull instanceof EntityType) {
            scoreValue = pastPlayer.getStatistic(this.trackedStatistic, (EntityType) statEntityOrMaterialOrNull);
          } else {
            scoreValue = pastPlayer.getStatistic(this.trackedStatistic);
          }
        } else if (this.customStatistic != null) {
          switch (this.customStatistic) {
            case LEVELS:
              scoreValue = PlayerDataManager.getXPLevels(pastPlayer);
              break;
            case FALL_FROM_HEIGHT:
              scoreValue = PlayerDataManager.getFallHeight(pastPlayer);
              break;
          }
        }

        if (this.fullStat != null) {
          scoreValue /= this.fullStat.getDivisionFactor();
        }

        Score score = objective.getScore(name);
        if (score.isScoreSet() || scoreValue > 0) {
          score.setScore(scoreValue);
        }

        if (scoreValue > 0) positiveValues = true;
      }

      Score deathsScore = this.deathsObjective.getScore(name);
      deathsScore.setScore(pastPlayer.getStatistic(Statistic.DEATHS));

      if(pastPlayer.getPlayer() != null) {
        Score healthScore = this.healthObjective.getScore(name);
        healthScore.setScore((int) pastPlayer.getPlayer().getHealth());
      }
    }

    return positiveValues;
  }

  public void addPlayer(@org.jetbrains.annotations.NotNull Player player) {
    player.setScoreboard(board);

    if(this.objective != null) {
      Bukkit.getLogger().info("Added player " + player.getName() + " to board " + getObjectiveName());

      TextComponent clearCommand = new TextComponent("/sidebar clear");
      clearCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sidebar clear"));
      clearCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run command")));

      TextComponent shuffleCommand = new TextComponent("/sidebar shuffle");
      shuffleCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sidebar shuffle"));
      shuffleCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run command")));

      TextComponent displayCommand = new TextComponent("/sidebar display");
      displayCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sidebar display "));
      displayCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to type command")));

      BaseComponent[] component = new ComponentBuilder("Displaying ").color(ChatColor.GREEN)
          .append(getObjectiveName()).color(ChatColor.WHITE).bold(true)
          .append(" on your sidebar. (").color(ChatColor.GREEN).bold(false)
          .append(clearCommand).color(ChatColor.AQUA).underlined(true)
          .append(" to clear, ").color(ChatColor.GREEN).underlined(false)
          .append(shuffleCommand).color(ChatColor.AQUA).underlined(true)
          .append(" to toggle shuffle, ").color(ChatColor.GREEN).underlined(false)
          .append(displayCommand).color(ChatColor.AQUA).underlined(true)
          .append(" to display something specific) ").color(ChatColor.GREEN).underlined(false).create();
      player.spigot().sendMessage(component);
//      player.sendMessage(ChatColor.GREEN + "Displaying " + ChatColor.WHITE + ChatColor.UNDERLINE + getObjectiveName() + ChatColor.RESET + ChatColor.GREEN + " on your sidebar. (/sidebar clear to clear, /sidebar shuffle to toggle shuffle, /sidebar display to display something specific)");
    }

    updateScoresForAllPlayers();

    PlayerDataManager.setScoreboard(player, this);
  }

  public Scoreboard getBoard() {
    return board;
  }

  public Statistic getTrackedStatistic() {
    return trackedStatistic;
  }

  public Object getStatEntityOrMaterialOrNull() {
    return statEntityOrMaterialOrNull;
  }

  public int getDivisionFactor() {
    if (this.fullStat == null) {
      return 1;
    } else {
      return fullStat.getDivisionFactor();
    }
  }

  public String getObjectiveName() {
    if (this.fullStat != null) {
      return this.fullStat.getNiceObjectiveName();
    } else if(this.customStatistic != null) {
      return this.customStatistic.name();
    }else{
      return null;
    }
  }
}

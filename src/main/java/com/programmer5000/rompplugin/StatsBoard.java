package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.logging.Logger;

public class StatsBoard {
  private final Scoreboard board;
  private final Statistic trackedStatistic;
  private final Objective objective;
  private final FullySpecifiedStatistic fullStat;
  private Object statEntityOrMaterialOrNull = null;

  StatsBoard(Statistic trackedStatistic, Object statEntityOrMaterialOrNull){
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null;
    this.board = manager.getNewScoreboard();
    this.trackedStatistic = trackedStatistic;
    this.statEntityOrMaterialOrNull = statEntityOrMaterialOrNull;
    this.fullStat = new FullySpecifiedStatistic(trackedStatistic, statEntityOrMaterialOrNull);

    Logger logger = Bukkit.getLogger();

    String objectiveKey = trackedStatistic.toString();

    if(statEntityOrMaterialOrNull != null){
      objectiveKey += "_" + statEntityOrMaterialOrNull.toString();
    }

    String objectiveName = getObjectiveName();
    logger.info("Creating new board & registering new objective with key " + objectiveKey + " and name " + objectiveName);
    this.objective = board.registerNewObjective(objectiveKey, "dummy", objectiveName);
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
  }

  public void updateScoresForAllPlayers() {
    OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();

    for (OfflinePlayer pastPlayer : allPlayers) {
      int scoreValue;
      if(this.statEntityOrMaterialOrNull instanceof Material){
        scoreValue = pastPlayer.getStatistic(this.trackedStatistic, (Material) statEntityOrMaterialOrNull);
      } else if(statEntityOrMaterialOrNull instanceof EntityType){
        scoreValue = pastPlayer.getStatistic(this.trackedStatistic, (EntityType) statEntityOrMaterialOrNull);
      } else{
        scoreValue = pastPlayer.getStatistic(this.trackedStatistic);
      }

      String name = pastPlayer.getName();
      if(name == null){
        name = pastPlayer.getUniqueId().toString();
      }

      scoreValue /= this.fullStat.getDivisionFactor();

      Score score = objective.getScore(name);
      score.setScore(scoreValue);
    }
  }

  public static void clearPlayer(@org.jetbrains.annotations.NotNull Player player){
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    assert manager != null; // should not be null
    player.setScoreboard(manager.getNewScoreboard());
  }

  public void addPlayer(@org.jetbrains.annotations.NotNull Player player){
    player.setScoreboard(board);
    Bukkit.getLogger().info("Added player " + player.getName() + " to board " + getObjectiveName());
    player.sendMessage("Added you to the " + getObjectiveName() + " scoreboard.");

    updateScoresForAllPlayers();
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

  public int getDivisionFactor(){
    return fullStat.getDivisionFactor();
  }

  public String getObjectiveName() {
    return this.fullStat.getNiceObjectiveName();
  }
}

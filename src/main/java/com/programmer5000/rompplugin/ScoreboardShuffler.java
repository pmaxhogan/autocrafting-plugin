package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScoreboardShuffler {
  public static final List<Player> players = new ArrayList<>();
  private static ScoreboardShuffler instance = null;
  StatsBoard shuffledStat = null;

  public static ScoreboardShuffler getInstance() {
    if (instance == null) {
      instance = new ScoreboardShuffler();
    }

    return instance;
  }

  public List<StatsBoard> getBoards() {
    List<StatsBoard> statsBoardList = new ArrayList<>();

    FileConfiguration config = RompPlugin.getInstance().getConfig();
    List<String> stringList = config.getStringList("shuffleStatistics");

    for (String boardName : stringList) {
      StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(boardName);

      if (statsBoard == null) {
        Bukkit.getLogger().warning("Could not find stats board from config: " + boardName);
      } else {
        statsBoardList.add(statsBoard);
      }
    }

    return statsBoardList;
  }

  private void updateShuffledStat(int attempts) {
    Random rand = new Random();
    List<StatsBoard> boards = getBoards();
    StatsBoard board = boards.get(rand.nextInt(boards.size()));
    boolean hadPositiveValues = board.updateScoresForAllPlayers();
    Bukkit.getLogger().info("Shuffling all scoreboards");

    if(attempts >= 10){
      Bukkit.getLogger().severe("Could not find a nonzero scoreboard! Please configure something better...");
    }

    if ((!hadPositiveValues || board == shuffledStat) && boards.size() > 1 && attempts < 10) {
      updateShuffledStat(attempts + 1);
    } else {
      shuffledStat = board;
    }
  }

  private void updateShuffledStat() {
    updateShuffledStat(0);
  }

  public void shuffleAll() {
    updateShuffledStat();

    for (Player player : players) {
      if (player.isOnline()) {
//        TODO: is this needed?
//        StatsBoard.clearPlayer(player);
        shuffledStat.addPlayer(player);
      }
    }
  }

  public void addPlayer(Player player) {
    if (player.isOnline()) {
      players.add(player);
      if (shuffledStat == null) updateShuffledStat();

//      TODO: is this needed?
//      StatsBoard.clearPlayer(player);
      shuffledStat.addPlayer(player);
    }
  }

  public void removePlayer(Player player) {
    players.remove(player);
    if (shuffledStat == null) updateShuffledStat();

    StatsBoardManager.getInstance().clearPlayer(player);
  }
}

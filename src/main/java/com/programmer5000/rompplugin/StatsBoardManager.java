package com.programmer5000.rompplugin;

import com.google.common.base.Stopwatch;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StatsBoardManager {
  private static StatsBoardManager instance = null;
  private final List<FullySpecifiedStatistic> allPossibleStatistics = new ArrayList<>();
  private final HashMap<FullySpecifiedStatistic, StatsBoard> boardMap = new HashMap<>();
  private final HashMap<FullySpecifiedStatistic.CustomStatistic, StatsBoard> boardMap2 = new HashMap<>();

  StatsBoardManager() {
    Logger logger = Bukkit.getLogger();

    for (Statistic stat : Statistic.values()) {
      switch (stat.getType()) {
        case ENTITY:// kill, killed by
          for (EntityType type : EntityType.values()) {
            Class<?> eClass = type.getEntityClass();
            if (eClass != null && Creature.class.isAssignableFrom(eClass) || eClass == Player.class) {
              FullySpecifiedStatistic fullStat = new FullySpecifiedStatistic(stat, type);
//              logger.info("Added entity " + fullStat.getNiceObjectiveName());
              allPossibleStatistics.add(fullStat);
            }
          }

          break;
        case ITEM:// drop, pickup, use, break, craft
        case BLOCK: // mine
          List<Material> allBlocks;
          if (stat.getType() == Statistic.Type.BLOCK) {
            allBlocks = Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList());
          } else {
            allBlocks = Arrays.stream(Material.values()).collect(Collectors.toList());
          }


          for (Material block : allBlocks) {
            FullySpecifiedStatistic fullStat = new FullySpecifiedStatistic(stat, block);
//            logger.info("Added " + fullStat.getNiceObjectiveName());
            allPossibleStatistics.add(fullStat);
          }
          break;
        case UNTYPED:
          FullySpecifiedStatistic fullStat = new FullySpecifiedStatistic(stat, null);
//          logger.info("Added " + fullStat.getNiceObjectiveName());
          allPossibleStatistics.add(fullStat);
          break;
      }
    }

    logger.info("Loaded " + allPossibleStatistics.size() + " possible statistics");
  }

  public static StatsBoardManager getInstance() {
    if (instance == null) {
      instance = new StatsBoardManager();
    }

    return instance;
  }

  public ArrayList<StatsBoard> getAllBoards() {
    ArrayList<StatsBoard> values = new ArrayList<>();
    values.addAll(boardMap.values());
    values.addAll(boardMap2.values());
    return values;
  }

  public void updateAll() {
    ArrayList<StatsBoard> allBoards = getAllBoards();
    Stopwatch timer = Stopwatch.createStarted();
    for (StatsBoard board : allBoards) {
      board.updateScoresForAllPlayers();
    }

    Bukkit.getLogger().info(allBoards.size() + ": updateAll method took: " + timer.stop());
  }

  public List<String> getPossibleStartingWith(String fragment) {
    List<String> returnVal = new ArrayList<>();
    for (FullySpecifiedStatistic thisFullStat : allPossibleStatistics) {
      if (thisFullStat.getNiceObjectiveName().startsWith(fragment)) {
        returnVal.add(thisFullStat.getNiceObjectiveName());
      }
    }

    for (FullySpecifiedStatistic.CustomStatistic stat : FullySpecifiedStatistic.CustomStatistic.values()) {
      returnVal.add(stat.getName());
    }
    return returnVal;
  }

  public StatsBoard getBoard(String board) {
    FullySpecifiedStatistic fullStat = null;
    for (FullySpecifiedStatistic thisFullStat : allPossibleStatistics) {
      if (thisFullStat.getNiceObjectiveName().equalsIgnoreCase(board)) {
        fullStat = thisFullStat;
        break;
      }
    }

    if (fullStat == null) {
      for (FullySpecifiedStatistic.CustomStatistic stat : FullySpecifiedStatistic.CustomStatistic.values()) {
        if (stat.getName().equalsIgnoreCase(board)) {
          return getBoard(stat);
        }
      }

      return null;
    } else {
      return getBoard(fullStat.trackedStatistic, fullStat.statEntityOrMaterialOrNull);
    }
  }

  public StatsBoard getBoard(FullySpecifiedStatistic.CustomStatistic customStat) {
    if (boardMap2.containsKey(customStat)) {
      return boardMap2.get(customStat);
    } else {
      StatsBoard board = new StatsBoard(customStat);

      boardMap2.put(customStat, board);

      return board;
    }
  }

  public StatsBoard getBoard(Statistic trackedStatistic, Object statEntityOrMaterialOrNull) {
    FullySpecifiedStatistic stat = new FullySpecifiedStatistic(trackedStatistic, statEntityOrMaterialOrNull);
    if (boardMap.containsKey(stat)) {
      return boardMap.get(stat);
    } else {
      StatsBoard board = new StatsBoard(trackedStatistic, statEntityOrMaterialOrNull);

      boardMap.put(stat, board);

      return board;
    }

  }
}

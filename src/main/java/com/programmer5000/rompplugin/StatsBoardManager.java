package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StatsBoardManager {
  private static StatsBoardManager instance = null;
  private final List<FullySpecifiedStatistic> allPossibleStatistics = new ArrayList<>();
  private final HashMap<FullySpecifiedStatistic, StatsBoard> boardMap = new HashMap<>();

  StatsBoardManager() {
    Logger logger = Bukkit.getLogger();

    for(Statistic stat : Statistic.values()) {
      switch(stat.getType()) {
        case ITEM:// drop, pickup, use, break, craft
        case ENTITY:// kill, killed by
          break;
        case BLOCK: // mine
          List<Material> allBlocks = Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList());
          for(Material block : allBlocks){
            FullySpecifiedStatistic fullStat = new FullySpecifiedStatistic(stat, block);
            logger.info("Added " + fullStat.getNiceObjectiveName());
            allPossibleStatistics.add(fullStat);
          }
          break;
        case UNTYPED:
          FullySpecifiedStatistic fullStat = new FullySpecifiedStatistic(stat, null);
          logger.info("Added " + fullStat.getNiceObjectiveName());
          allPossibleStatistics.add(fullStat);
          break;
      }
    }

    logger.info("Loaded " + allPossibleStatistics.size() + " possible statistics");
  }

  public Collection<StatsBoard> getAllBoards(){
    return boardMap.values();
  }

  public void updateAll(){
    for(StatsBoard board : getAllBoards()){
      board.updateScoresForAllPlayers();
    }
  }

  public StatsBoard getBoard(Statistic trackedStatistic, Object statEntityOrMaterialOrNull) {
    FullySpecifiedStatistic stat = new FullySpecifiedStatistic(trackedStatistic, statEntityOrMaterialOrNull);
    if(boardMap.containsKey(stat)){
      return boardMap.get(stat);
    }else{
      StatsBoard board = new StatsBoard(trackedStatistic, statEntityOrMaterialOrNull);

      boardMap.put(stat, board);

      return board;
    }

  }

  public static StatsBoardManager getInstance(){
    if(instance == null) {
      instance = new StatsBoardManager();
    }

    return instance;
  }
}

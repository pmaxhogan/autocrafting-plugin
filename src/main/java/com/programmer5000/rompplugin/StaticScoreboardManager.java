package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

public class StaticScoreboardManager {
  private static final StaticScoreboardManager instance = new StaticScoreboardManager();
  private Scoreboard board;

  public static StaticScoreboardManager getInstance() {
    return instance;
  }

  private StaticScoreboardManager(){
//    ScoreboardManager manager = Bukkit.getScoreboardManager();
//    assert manager != null;
//    board = manager.getNewScoreboard();
//
//    Objective obj = board.registerNewObjective("Health", "health", "§c♥");
//    obj.setRenderType(RenderType.HEARTS);
//    obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
  }

  public void addToBoard(@NotNull Scoreboard board){
    Objective obj = board.registerNewObjective("Health", "health", "§c♥");
    obj.setRenderType(RenderType.HEARTS);
    obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

    Objective obj2 = board.registerNewObjective("Deaths", "deaths", "§c♥");
    obj2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
  }

//  public void addPlayer(@NotNull Player player){
//    player.setScoreboard(board);
//  }
}

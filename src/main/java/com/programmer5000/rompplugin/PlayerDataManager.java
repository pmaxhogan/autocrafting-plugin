package com.programmer5000.rompplugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerDataManager {
//  static final NamespacedKey fallHeightKey = new NamespacedKey(SpigotPlugin.getInstance(), "max-fall-height");

  public static Boolean getShuffle(Player player) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    return (Boolean) config.get("playerShuffle." + player.getUniqueId(), config.getBoolean("shuffleEnabledByDefault"));
  }

  public static void setShuffle(Player player, Boolean shuffle) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    config.set("playerShuffle." + player.getUniqueId(), shuffle);
    RompPlugin.getInstance().saveConfig();
  }

  public static void setScoreboard(Player player, StatsBoard board) {
    String searchStr ;
    if(board == null || board.getObjectiveName() == null){
      searchStr = "";
    }else{
      searchStr = board.getObjectiveName();
    }

    FileConfiguration config = RompPlugin.getInstance().getConfig();
    config.set("playerScoreboard." + player.getUniqueId(), searchStr);
    RompPlugin.getInstance().saveConfig();
  }

  public static StatsBoard getScorebaord(Player player) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    String searchStr = (String) config.get("playerScoreboard." + player.getUniqueId());

    if (searchStr != null) {
      return StatsBoardManager.getInstance().getBoard(searchStr);
    } else {
      return null;
    }
  }

  public static void addFallHeight(Player player, int fallHeight) {
    if (getFallHeight(player) >= fallHeight) return;

    FileConfiguration config = RompPlugin.getInstance().getConfig();
    config.set("playerFallHeight." + player.getUniqueId(), fallHeight);
    RompPlugin.getInstance().saveConfig();
  }


  public static int getFallHeight(OfflinePlayer player) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    return (int) config.get("playerFallHeight." + player.getUniqueId(), 0);
  }

  public static void setXPLevels(Player player, int levels) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    config.set("playerXPLevels." + player.getUniqueId(), levels);
    RompPlugin.getInstance().saveConfig();
  }


  public static int getXPLevels(OfflinePlayer player) {
    FileConfiguration config = RompPlugin.getInstance().getConfig();
    return (int) config.get("playerXPLevels." + player.getUniqueId(), 0);
  }
}

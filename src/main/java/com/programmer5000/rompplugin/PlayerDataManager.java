package com.programmer5000.rompplugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerDataManager {
//  static final NamespacedKey fallHeightKey = new NamespacedKey(SpigotPlugin.getInstance(), "max-fall-height");

  public static Boolean getShuffle(Player player) {
    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    return (Boolean) config.get("playerShuffle." + player.getUniqueId(), config.getBoolean("shuffleEnabledByDefault"));
  }

  public static void setShuffle(Player player, Boolean shuffle){
    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    config.set("playerShuffle." + player.getUniqueId(), shuffle);
    SpigotPlugin.getInstance().saveConfig();
  }

  public static void setScoreboard(Player player, StatsBoard board) {
    String searchStr = board == null ? "" : board.getObjectiveName();

    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    config.set("playerScoreboard." + player.getUniqueId(), searchStr);
    SpigotPlugin.getInstance().saveConfig();
  }

  public static StatsBoard getScorebaord(Player player){
    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    String searchStr = (String) config.get("playerScoreboard." + player.getUniqueId());

    if (searchStr != null) {
      return StatsBoardManager.getInstance().getBoard(searchStr);
    }else{
      return null;
    }
  }

    public static void addFallHeight(Player player, int fallHeight){
    if(getFallHeight(player) >= fallHeight) return;

//    Bukkit.getLogger().info(player.getDisplayName() + " fell from height " + fallHeight);

//    player.getPersistentDataContainer().set(fallHeightKey, PersistentDataType.INTEGER, fallHeight);
    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    config.set("playerFallHeight." + player.getUniqueId(), fallHeight);
    SpigotPlugin.getInstance().saveConfig();
  }


  public static int getFallHeight(OfflinePlayer player){
//    if(player.getPersistentDataContainer().has(fallHeightKey, PersistentDataType.INTEGER)) {
//      //noinspection ConstantConditions
//      return player.getPersistentDataContainer().get(fallHeightKey, PersistentDataType.INTEGER);
//    }else{
//      return 0;
//    }
    FileConfiguration config = SpigotPlugin.getInstance().getConfig();
    return (int) config.get("playerFallHeight." + player.getUniqueId(), 0);
  }
}

package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerDataManager {
//  static final NamespacedKey fallHeightKey = new NamespacedKey(SpigotPlugin.getInstance(), "max-fall-height");


  public static void addFallHeight(Player player, int fallHeight){
    if(getFallHeight(player) >= fallHeight) return;

    Bukkit.getLogger().info(player.getDisplayName() + " fell from height " + fallHeight);

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

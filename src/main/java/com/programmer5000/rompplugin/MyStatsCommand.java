package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MyStatsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Logger logger = Bukkit.getLogger();


    if (sender instanceof Player) {
      Player player = (Player) sender;


      if(args.length == 1 && args[0].equalsIgnoreCase("clear")){
        StatsBoard.clearPlayer(player);

        player.sendMessage("Cleared your scoreboard.");

        return true;
      }

      Statistic stat = Statistic.ENTITY_KILLED_BY;
      Object statEntityOrMaterialOrNull = EntityType.ZOMBIE;

      StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(stat, statEntityOrMaterialOrNull);
      statsBoard.addPlayer(player);




      /*for (OfflinePlayer pastPlayer : allPlayers) {
        for(Statistic stat : Statistic.values()) {
          switch(stat.getType()){
            case ITEM:// drop, pickup, use, break, craft
            case ENTITY:// kill, killed by
              logger.info(stat.getType() + " " + stat);
              break;
            case BLOCK:
              List<Material> allBlocks = Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList());
//              Material block = allBlocks.get(0);
              Material block = Material.DIRT;


              player.sendMessage(pastPlayer.getName() + " did block stat: "  + stat.name()  + " on block " + block.getKey() + " with value " + pastPlayer.getStatistic(stat, block));
              break;
            case UNTYPED:
              player.sendMessage(pastPlayer.getName() + " did stat: "  + stat.name() + " with value " + pastPlayer.getStatistic(stat));
          }
        }
      }*/

      return true;
    }else{
      return false;
    }
  }
}

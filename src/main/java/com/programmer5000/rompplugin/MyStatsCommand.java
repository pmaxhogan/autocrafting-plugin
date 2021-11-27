package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Logger;

public class MyStatsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player) {
      Logger logger = Bukkit.getLogger();

      Player player = (Player) sender;


      if(args.length == 1 && args[0].equalsIgnoreCase("clear")){
        StatsBoard.clearPlayer(player);

        player.sendMessage("Cleared your scoreboard.");

        return true;
      }else if(args.length > 1 && args[0].equalsIgnoreCase("display")) {
        String searchStr = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if(searchStr.startsWith("\"") && searchStr.endsWith("\"")){
          searchStr = searchStr.substring(1, searchStr.length() - 1);
        }

//      Statistic stat = Statistic.ENTITY_KILLED_BY;
//      Object statEntityOrMaterialOrNull = EntityType.ZOMBIE;
//
//      StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(stat, statEntityOrMaterialOrNull);

        StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(searchStr);

        if(statsBoard == null){
          player.sendMessage("Could not find scoreboard " + searchStr);
        }else {
          statsBoard.addPlayer(player);
        }

        return true;
      }else{
        return false;
      }
    }else{
      return false;
    }
  }
}

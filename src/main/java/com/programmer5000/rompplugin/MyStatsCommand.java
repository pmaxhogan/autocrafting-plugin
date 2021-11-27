package com.programmer5000.rompplugin;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MyStatsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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


      return true;
    }else{
      return false;
    }
  }
}

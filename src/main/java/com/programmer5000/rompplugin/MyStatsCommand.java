package com.programmer5000.rompplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MyStatsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;


      if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
        StatsBoard.clearPlayer(player);

        player.sendMessage("Cleared your scoreboard.");

        return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("shuffle")) {
        Boolean shuffledNow = PlayerDataManager.getShuffle(player);
        PlayerDataManager.setShuffle(player, !shuffledNow);

        player.sendMessage(!shuffledNow ? "Enabled sidebar shuffling" : "Disabled sidebar shuffling");

        if (!shuffledNow) {
          ScoreboardShuffler.getInstance().addPlayer(player);
        } else {
          ScoreboardShuffler.getInstance().removePlayer(player);
        }

        return true;
      } else if (args.length > 1 && args[0].equalsIgnoreCase("display")) {
        String searchStr = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (searchStr.startsWith("\"") && searchStr.endsWith("\"")) {
          searchStr = searchStr.substring(1, searchStr.length() - 1);
        }

        StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(searchStr);

        if (statsBoard == null) {
          player.sendMessage("Could not find scoreboard " + searchStr);
        } else {
          Boolean shuffledNow = PlayerDataManager.getShuffle(player);
          if (shuffledNow) {
            PlayerDataManager.setShuffle(player, false);
            player.sendMessage("Disabled sidebar shuffling (/sidebar shuffle to reenable)");
            ScoreboardShuffler.getInstance().removePlayer(player);
          }

          statsBoard.addPlayer(player);
        }

        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}

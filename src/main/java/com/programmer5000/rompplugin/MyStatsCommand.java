package com.programmer5000.rompplugin;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
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
        StatsBoardManager.getInstance().clearPlayer(player);

        stopShuffling(player);

        player.sendMessage(ChatColor.GREEN + "Cleared your scoreboard.");

        return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("shuffle")) {
        Boolean shuffledNow = PlayerDataManager.getShuffle(player);
        PlayerDataManager.setShuffle(player, !shuffledNow);

        if (!shuffledNow) {
          ScoreboardShuffler.getInstance().addPlayer(player);
        } else {
          ScoreboardShuffler.getInstance().removePlayer(player);
        }


        player.sendMessage(!shuffledNow ? ChatColor.GREEN + "Enabled sidebar shuffling" : ChatColor.GREEN + "Disabled sidebar shuffling");

        return true;
      } else if (args.length > 1 && args[0].equalsIgnoreCase("display")) {
        String searchStr = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (searchStr.startsWith("\"") && searchStr.endsWith("\"")) {
          searchStr = searchStr.substring(1, searchStr.length() - 1);
        }

        StatsBoard statsBoard = StatsBoardManager.getInstance().getBoard(searchStr);

        if (statsBoard == null) {
          player.sendMessage(ChatColor.RED + "Could not find scoreboard " + searchStr);
        } else {
          stopShuffling(player);

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

  private void stopShuffling(Player player) {
    Boolean shuffledNow = PlayerDataManager.getShuffle(player);
    if (shuffledNow) {
      PlayerDataManager.setShuffle(player, false);

      TextComponent shuffleCommand = new TextComponent("/sidebar shuffle");
      shuffleCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sidebar shuffle"));
      shuffleCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run command")));

      BaseComponent[] component = new ComponentBuilder("Disabled sidebar shuffling (").color(net.md_5.bungee.api.ChatColor.GREEN)
          .append(shuffleCommand).color(net.md_5.bungee.api.ChatColor.AQUA).underlined(true)
          .append(" to reenable)").color(net.md_5.bungee.api.ChatColor.GREEN).underlined(false).create();

      player.spigot().sendMessage(component);

      ScoreboardShuffler.getInstance().removePlayer(player);
    }
  }
}

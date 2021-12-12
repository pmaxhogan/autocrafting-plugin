package com.programmer5000.rompplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReshuffleCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    ScoreboardShuffler.getInstance().shuffleAll();

    sender.sendMessage(ChatColor.GREEN + "Shuffled sidebar.");

    return true;
  }
}

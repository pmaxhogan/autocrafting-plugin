package com.programmer5000.rompplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    RompPlugin.getInstance().reloadConfig();

    RompPlugin.getInstance().scheduleShuffleTask();

    sender.sendMessage(ChatColor.GREEN + "Reloaded config.");

    return true;
  }
}

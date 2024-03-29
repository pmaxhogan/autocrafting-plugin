package com.programmer5000.rompplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatsTabCompleter implements TabCompleter {
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
    ArrayList<String> commandsList = new ArrayList<>();
    commandsList.add("display");
    commandsList.add("shuffle");
    commandsList.add("clear");

    ArrayList<String> paramaterizedDisplayTypes = new ArrayList<>();
    paramaterizedDisplayTypes.add("Mined:");
    paramaterizedDisplayTypes.add("Broke:");
    paramaterizedDisplayTypes.add("Crafted:");
    paramaterizedDisplayTypes.add("Dropped:");
    paramaterizedDisplayTypes.add("Used:");
    paramaterizedDisplayTypes.add("Killed:");
    paramaterizedDisplayTypes.add("Killed by:");
    paramaterizedDisplayTypes.add("Interacted:");


    if (args.length > 1 && Objects.equals(args[0], "display")) {
      String fragment = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

      List<String> results = new ArrayList<>();

      if (!fragment.contains(":")) {
        results.addAll(paramaterizedDisplayTypes.stream().filter(str -> str.startsWith(fragment)).collect(Collectors.toList()));
      }

      results.addAll(StatsBoardManager.getInstance().getPossibleStartingWith(fragment).stream().filter(str -> (fragment.contains(":") || !str.contains(":")) && str.startsWith(fragment)).map(str -> {
        if (fragment.contains(" ")) {
          return str.substring(Math.min(str.length(), fragment.lastIndexOf(" ") + 1));
        } else {
          return str;
        }
      }).collect(Collectors.toList()));

      return results;
    } else if (args.length == 1) {
      return commandsList;
    } else {
      return new ArrayList<>();
    }
  }
}

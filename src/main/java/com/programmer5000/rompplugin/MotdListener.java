package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class MotdListener implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void motdListener(final ServerListPingEvent event) {
    try {
      File files = new File(SpigotPlugin.getInstance().getDataFolder().getAbsolutePath() + "/motds.txt");
      Scanner scanner = new Scanner(files);
      List<String> options = new ArrayList<>();
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (!line.isEmpty()) options.add(line);
      }

      if(new Random().nextInt(100) > 98){
        try {
          File file = new File(SpigotPlugin.getInstance().getDataFolder().getAbsolutePath() + "/troll.png");
          event.setServerIcon(Bukkit.loadServerIcon(file));
        }catch(Exception e){
          e.printStackTrace();
        }

        event.setMotd("Your IP address is " + event.getAddress().getHostAddress() + "\nI am rapidly approaching your location");
      }else {
        String motdBase = SpigotPlugin.getInstance().getServer().getMotd();
        String secondLine = options.get(new Random().nextInt(options.size()));
        // make message white
        event.setMotd(motdBase + "\n\u00A7f" + secondLine);
      }
    } catch (FileNotFoundException e) {
      String errorStr = "Could not find file!\nAdd motds.txt to ./plugins/" + SpigotPlugin.getInstance().getDataFolder().getName();
      Bukkit.getLogger().info(errorStr + e);
      event.setMotd(errorStr);
    }
  }
}

package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SpigotPlugin extends JavaPlugin {
  private static SpigotPlugin instance;
  private int task = -1;
  private Map<RecipeShape, ShapedRecipe> shapedRecipeMap;
  private Map<List<ItemStack>, ShapelessRecipe> shapelessRecipeMap;

  public static SpigotPlugin getInstance() {
    return instance;
  }

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  public Map<RecipeShape, ShapedRecipe> getShapedRecipeMap() {
    return shapedRecipeMap;
  }

  public Map<List<ItemStack>, ShapelessRecipe> getShapelessRecipeMap() {
    return shapelessRecipeMap;
  }

  private ItemStack[] shapedRecipeToItemStack(ShapedRecipe shapedRecipe) {
    ItemStack[] items = new ItemStack[9];

    int i;
    int row = 0;
    for (String s : shapedRecipe.getShape()) {
      i = 0;
      for (char c : s.toCharArray()) {
        items[i + row * 3] = shapedRecipe.getIngredientMap().get(c);
        i++;
      }
      row++;
    }

    return items;
  }


  @Override
  public void onEnable() {
    FileConfiguration config = this.getConfig();
    config.addDefault("enableRecipeEmpty", false);
    config.options().copyDefaults(true);
    saveConfig();

    for (Player player : Bukkit.getOnlinePlayers()) {
      ChestListener.handlePlayerJoin(player);
    }


    instance = this;

    shapedRecipeMap = new HashMap<>();
    shapelessRecipeMap = new HashMap<>();

//        Bukkit.getLogger().info("calculating recipe maps...");
    Iterator<Recipe> iterator = Bukkit.recipeIterator();
    while (iterator.hasNext()) {
      Recipe recipe = iterator.next();
      if (recipe instanceof ShapedRecipe) {
        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        RecipeShape recipeShape = new RecipeShape(shapedRecipeToItemStack(shapedRecipe));

        shapedRecipeMap.put(recipeShape, shapedRecipe);
      }
      if (recipe instanceof ShapelessRecipe) {
        ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
        shapelessRecipeMap.put(shapelessRecipe.getIngredientList(), shapelessRecipe);
      }
    }

    Bukkit.getLogger().info("calculated " + shapedRecipeMap.size() + " recipe maps!");

    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    Objects.requireNonNull(getCommand("sbar-reload")).setExecutor(new ReloadCommand());
    Objects.requireNonNull(getCommand("sidebar")).setExecutor(new MyStatsCommand());
    Objects.requireNonNull(getCommand("sidebar")).setTabCompleter(new StatsTabCompleter());

    getServer().getPluginManager().registerEvents(new ChestListener(), this);
    getServer().getPluginManager().registerEvents(new MotdListener(), this);

    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> StatsBoardManager.getInstance().updateAll(), 0, 20 * 15);// run once every 15 seconds

    scheduleShuffleTask();
  }

  public void scheduleShuffleTask() {
    if(task != -1){
      Bukkit.getScheduler().cancelTask(task);
    }

    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> ScoreboardShuffler.getInstance().shuffleAll(), 0, 20 * 60 * this.getConfig().getLong("scheduleShuffleInterval", 10));// run once every n minutes
  }
}

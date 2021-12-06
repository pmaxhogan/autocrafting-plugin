package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SpigotPlugin extends JavaPlugin {
    private static SpigotPlugin instance;
    public static final SpigotPlugin getInstance() {
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

    private Map<RecipeShape, ShapedRecipe> shapedRecipeMap;
    private Map<List<ItemStack>, ShapelessRecipe> shapelessRecipeMap;

    private ItemStack[] shapedRecipeToItemStack(ShapedRecipe shapedRecipe){
        ItemStack[] items = new ItemStack[9];

        int i = 0;
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
        config.addDefault("enableRecipeEmpty", true);
        config.options().copyDefaults(true);
        saveConfig();



        instance = this;

        shapedRecipeMap = new HashMap();
        shapelessRecipeMap = new HashMap();

//        Bukkit.getLogger().info("calculating recipe maps...");
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while(iterator.hasNext()){
            Recipe recipe = iterator.next();
            if(recipe instanceof ShapedRecipe){
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                RecipeShape recipeShape = new RecipeShape(shapedRecipeToItemStack(shapedRecipe));

                shapedRecipeMap.put(recipeShape, shapedRecipe);
            }
            if(recipe instanceof ShapelessRecipe){
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                shapelessRecipeMap.put(shapelessRecipe.getIngredientList(), shapelessRecipe);
            }
        }

        Bukkit.getLogger().info("calculated " + shapedRecipeMap.size() + " recipe maps!");

        // Don't log enabling, Spigot does that for you automatically!

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("sidebar").setExecutor(new MyStatsCommand());
        getCommand("sidebar").setTabCompleter(new StatsTabCompleter());

        getServer().getPluginManager().registerEvents(new ChestListener(), this);
        getServer().getPluginManager().registerEvents(new MotdListener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                StatsBoardManager.getInstance().updateAll();
            }
        }, 0, 20);// run once a second (every 20 ticks)
    }
}

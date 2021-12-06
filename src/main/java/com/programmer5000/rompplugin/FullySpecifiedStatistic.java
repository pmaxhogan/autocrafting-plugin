package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class FullySpecifiedStatistic {
  public Statistic trackedStatistic = null;
  public CustomStatistic customStatistic = null;
  public Object statEntityOrMaterialOrNull = null;
  private final int hashCode;


  @SuppressWarnings("unused")
  enum StatisticsToNames {
    DEATHS,
    MOB_KILLS,
    PLAYER_KILLS,
    FISH_CAUGHT,
    ANIMALS_BRED,
    LEAVE_GAME("Games Left"),
    JUMP("Times Jumped"),
    DROP_COUNT("Items Dropped"),
    DROP("Dropped"),
    PICKUP("Picked Up"),
    PLAY_ONE_MINUTE("Minutes Played", 20 * 60),
    TIME_SINCE_DEATH("Minutes Since Last Death", 20 * 60),
    TIME_SINCE_REST("Minutes Since Last Rest", 20 * 60),
    WALK_ONE_CM("Distance Walked", 100),
    WALK_ON_WATER_ONE_CM("Distance Walked Through Water", 100),
    FALL_ONE_CM("Distance Fallen", 100),
    SNEAK_TIME("Distance Sneaked", 100),
    CLIMB_ONE_CM("Distance Climbed", 100),
    FLY_ONE_CM("Distance Flown", 100),
    WALK_UNDER_WATER_ONE_CM("Distance Walked Underwater", 100),
    MINECART_ONE_CM("Distance by Minecart", 100),
    BOAT_ONE_CM("Distance by Boat", 100),
    PIG_ONE_CM("Distance by Pig", 100),
    HORSE_ONE_CM("Distance by Horse", 100),
    SPRINT_ONE_CM("Distance Sprinted", 100),
    CROUCH_ONE_CM("Distance Crouched", 100),
    SWIM_ONE_CM("Distance Swam", 100),
    AVIATE_ONE_CM("Distance by Elytra", 100),
    STRIDER_ONE_CM("Distance by Strider", 100),
    MINE_BLOCK("Mined"),
    USE_ITEM("Used"),
    BREAK_ITEM("Broke"),
    CRAFT_ITEM("Crafted"),
    KILL_ENTITY("Killed"),
    ENTITY_KILLED_BY("Killed by"),
    TALKED_TO_VILLAGER("Villagers Talked To"),
    TRADED_WITH_VILLAGER("Villager Trades"),
    CAKE_SLICES_EATEN,
    CAULDRON_FILLED("Cauldrons Filled"),
    CAULDRON_USED("Cauldrons Used"),
    ARMOR_CLEANED("Armor Pieces Cleaned"),
    BANNER_CLEANED("Banners Cleaned"),
    BREWINGSTAND_INTERACTION("Interacted: Brewing Stands"),
    BEACON_INTERACTION("Interactions with Beacon"),
    DROPPER_INSPECTED("Interacted: Droppers"),
    HOPPER_INSPECTED("Interacted: Hoppers"),
    INTERACT_WITH_LECTERN("Interacted: Lecterns"),
    DISPENSER_INSPECTED("Interacted: Dispensers"),
    OPEN_BARREL("Interacted: Barrels"),
    NOTEBLOCK_PLAYED("Noteblocks Played"),
    NOTEBLOCK_TUNED("Noteblocks Tuned"),
    FLOWER_POTTED("Flowers Potted"),
    TRAPPED_CHEST_TRIGGERED("Trapped Chests Triggered"),
    ENDERCHEST_OPENED("Interacted: Enderchests"),
    ITEM_ENCHANTED("Items Enhcanted"),
    RECORD_PLAYED("Discs Played"),
    FURNACE_INTERACTION("Interacted: Furnaces"),
    CRAFTING_TABLE_INTERACTION("Interacted: Crafting Tables"),
    CHEST_OPENED("Interacted: Chests"),
    SLEEP_IN_BED("Times Slept"),
    SHULKER_BOX_OPENED("Interacted: Boxes"),
    DAMAGE_DEALT(10),
    DAMAGE_TAKEN(10),
    DAMAGE_DEALT_ABSORBED("Dealt Damage Absorbed", 10),
    DAMAGE_DEALT_RESISTED("Dealt Damage Resisted", 10),
    DAMAGE_BLOCKED_BY_SHIELD("Damage Blocked by Shield", 10),
    DAMAGE_ABSORBED("Damage Absorbed", 10),
    DAMAGE_RESISTED("Damage Resisted", 10),
    INTERACT_WITH_BLAST_FURNACE("Interacted: Blast Furnace"),
    INTERACT_WITH_SMOKER("Interacted: Smoker"),
    INTERACT_WITH_CAMPFIRE("Interacted: Campfire"),
    INTERACT_WITH_CARTOGRAPHY_TABLE("Interacted: Cartography Table"),
    INTERACT_WITH_LOOM("Interacted: Loom"),
    INTERACT_WITH_STONECUTTER("Interacted: Stonecutter"),
    INTERACT_WITH_ANVIL("Interacted: Anvil"),
    INTERACT_WITH_GRINDSTONE("Interacted: Grindstone"),
    INTERACT_WITH_SMITHING_TABLE("Interacted: Smithing Table"),

    BELL_RING("Bells Rung"),
    RAID_TRIGGER("Raids Started"),
    RAID_WIN("Raids Won"),
    TARGET_HIT("Targets Hit");

    private final String name;
    private final int divisionFactor;

    private StatisticsToNames() {
      this.name = keyToString(this.toString());
      this.divisionFactor = 1;
    }

    private StatisticsToNames(String name) {
      this.name = name;
      this.divisionFactor = 1;
    }

    private StatisticsToNames(int divisionFactor) {
      this.name = keyToString(this.toString());
      this.divisionFactor = divisionFactor;
    }

    private StatisticsToNames(String name, int divisionFactor) {
      this.name = name;
      this.divisionFactor = divisionFactor;
    }

    public int getDivisionFactor() {
      return divisionFactor;
    }
  }

  enum CustomStatistic{
    FALL_FROM_HEIGHT("Max Fall Height"),
    KDR("KDR Percent"),
    PLAYER_KDR("Player KDR Percent");

    private final String name;
    CustomStatistic(String name){
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  FullySpecifiedStatistic(Statistic trackedStatistic, Object statEntityOrMaterialOrNull){
    this.trackedStatistic = trackedStatistic;
    this.statEntityOrMaterialOrNull = statEntityOrMaterialOrNull;
    this.hashCode = Objects.hash(trackedStatistic, statEntityOrMaterialOrNull);
  }

  FullySpecifiedStatistic(CustomStatistic customStat){
    this.customStatistic = customStat;
    this.hashCode = Objects.hash(customStat);
  }

  public static String toTitleCase(String input) {
    StringBuilder titleCase = new StringBuilder(input.length());
    boolean nextTitleCase = true;

    for (char c : input.toCharArray()) {
      if (Character.isSpaceChar(c)) {
        nextTitleCase = true;
      } else if (nextTitleCase) {
        c = Character.toTitleCase(c);
        nextTitleCase = false;
      }

      titleCase.append(c);
    }

    return titleCase.toString();
  }

  private static String keyToString(String key){
    return toTitleCase(key.replaceAll("_", " ").toLowerCase(Locale.ROOT));
  }

  private StatisticsToNames getStatisticsToNames(){
    try {
      return StatisticsToNames.valueOf(trackedStatistic.toString());
    }catch(IllegalArgumentException ignored){
      return null;
    }
  }

  private String getStatisticName(){
    if(this.trackedStatistic == null) {
      return this.customStatistic.name;
    }else{
      if(getStatisticsToNames() != null) {
        return getStatisticsToNames().name;
      }else{
        return null;
      }
    }
  }

  public int getDivisionFactor(){
    if(this.trackedStatistic == null) {
      return 1;
    }else {
      return getStatisticsToNames().getDivisionFactor();
    }
  }

  String getNiceObjectiveName(){
    Logger logger = Bukkit.getLogger();
    String thisStatisticName = null;

    if(this.trackedStatistic != null) {
      thisStatisticName = trackedStatistic.getKey().toString();
    }

    String name = getStatisticName();
    if(name != null){
      thisStatisticName = name;
    }

    if(statEntityOrMaterialOrNull == null){
      return thisStatisticName;
    }else{
      if (statEntityOrMaterialOrNull instanceof Material) {
        Material mat = (Material) statEntityOrMaterialOrNull;


        return thisStatisticName + ": " + keyToString(mat.getKey().getKey());
      }else if (statEntityOrMaterialOrNull instanceof EntityType) {
        EntityType type = (EntityType) statEntityOrMaterialOrNull;

        return thisStatisticName + ": " + keyToString(type.getKey().getKey());
      } else {
        return thisStatisticName + " " + statEntityOrMaterialOrNull;
      }
    }
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FullySpecifiedStatistic that = (FullySpecifiedStatistic) o;
    return trackedStatistic == that.trackedStatistic && statEntityOrMaterialOrNull == that.statEntityOrMaterialOrNull;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }
}

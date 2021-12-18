package com.programmer5000.rompplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

class RecipeShape {
  private final ItemStack[] items;

  public RecipeShape(ItemStack[] items) {
    this.items = items;
  }

  public int hashCode() {
    return Objects.hash(items);
  }

  public boolean equals(Object o) {
    return o instanceof RecipeShape && o.hashCode() == hashCode();
  }
}

public class ChestListener implements Listener {
  final int[] allRows = {0, 1, 2, 9, 10, 11, 18, 19, 20};

  public static void handlePlayerJoin(Player player) {
    Boolean shuffledNow = PlayerDataManager.getShuffle(player);
    if (shuffledNow) {
      ScoreboardShuffler.getInstance().addPlayer(player);
    }else{
      StatsBoard board = PlayerDataManager.getScorebaord(player);
      if (board != null) {
        board.addPlayer(player);
      } else {
        StatsBoardManager.getInstance().clearPlayer(player);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    Player player = event.getPlayer();
    handlePlayerJoin(player);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInventoryClick(final InventoryClickEvent event) {
//    Bukkit.getLogger().info("inventory click event");
    boolean isChest = event.getInventory().getType() == InventoryType.CHEST;
    if (!isChest) return;
    if (checkInventoryIsSpecialChest(event.getInventory())) return;

    boolean slotIsContainer = event.getSlotType() == InventoryType.SlotType.CONTAINER;
    boolean clickedChest = (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CHEST);

    boolean otherInventoryMove = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.COLLECT_TO_CURSOR;

    int slot = event.getSlot();
    boolean isOkSlot = contains(allRows, slot);

    if (slotIsContainer && clickedChest && !isOkSlot || otherInventoryMove) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerExpChange(final PlayerLevelChangeEvent event){
    PlayerDataManager.setXPLevels(event.getPlayer(), event.getPlayer().getLevel());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDamage(final EntityDamageEvent event) {
    if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();

      PlayerDataManager.addFallHeight(player, (int) Math.floor((event.getDamage()) + 3));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryDrag(final InventoryDragEvent event) {
//    Bukkit.getLogger().info("inventory drag event");
    boolean isChest = event.getInventory().getType() == InventoryType.CHEST;
    if (!isChest) return;
    if (checkInventoryIsSpecialChest(event.getInventory())) return;

    boolean clickedChest = event.getInventory().getType() == InventoryType.CHEST;
    Set<Integer> slots = event.getInventorySlots();
    boolean isOkSlot = true;

    for (Integer i : slots) {
      if (!contains(allRows, i)) {
        isOkSlot = false;
        break;
      }
    }

    if (clickedChest && !isOkSlot) {
      event.setCancelled(true);
    }
  }

  private boolean checkInventoryIsSpecialChest(Inventory inventory) {
    try {
      return inventory.getType() != InventoryType.CHEST || inventory instanceof DoubleChest || inventory.getHolder() == null || ((Chest) inventory.getHolder()).getBlock().getType() != Material.TRAPPED_CHEST;
    } catch (java.lang.ClassCastException ignored) {
      return true;
    }
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryMove(final InventoryMoveItemEvent event) {
//    Bukkit.getLogger().info("inventory move event");
    Inventory sourceInventory = event.getSource();
    Inventory destInventory = event.getDestination();

    boolean originatesFromChest = sourceInventory.getType() == InventoryType.CHEST;
    boolean goesToChest = destInventory.getType() == InventoryType.CHEST;

    boolean originatesFromHopper = sourceInventory.getType() == InventoryType.HOPPER;
    boolean goesToHopper = destInventory.getType() == InventoryType.HOPPER;

    boolean chestToHopper = originatesFromChest && goesToHopper;
    boolean hopperToChest = originatesFromHopper && goesToChest;

    if (!chestToHopper && !hopperToChest) return;

    if (hopperToChest) {
//      Bukkit.getLogger().info("hopper to chest");
      if (checkInventoryIsSpecialChest(destInventory)) return;

      ItemStack stack = event.getItem().clone();
      stack.setAmount(1);


      ItemStack[] contents = destInventory.getContents();

      int smallestStackOfThisItem = 999999;
      int smallestStackOfThisItemPos = -1;
      for (int i = 0; i < contents.length; i++) {
        // this slot is empty, so it's not part of the template
        if (contents[i] == null) continue;

        boolean isValidSlot = contains(allRows, i);
        boolean canStack = contents[i].isSimilar(stack);
        boolean isSmallerStack = contents[i].getAmount() < smallestStackOfThisItem;
        if (isValidSlot && canStack && isSmallerStack) {
          smallestStackOfThisItemPos = i;
          smallestStackOfThisItem = contents[i].getAmount();
        }
      }

      if (smallestStackOfThisItemPos != -1 && smallestStackOfThisItem < stack.getMaxStackSize()) {
        ItemStack newStackHere = contents[smallestStackOfThisItemPos].clone();
        newStackHere.setAmount(newStackHere.getAmount() + 1);
        destInventory.setItem(smallestStackOfThisItemPos, newStackHere);

        event.getItem().setAmount(0);
      } else {
        event.setCancelled(true);
      }
    }
    if (chestToHopper) {
      if (checkInventoryIsSpecialChest(sourceInventory)) return;

      ItemStack[] stack = new ItemStack[9];
      ArrayList<ItemStack> stackList = new ArrayList<>();

      int pos = 0;
      for (int i : allRows) {
        ItemStack thisItem = sourceInventory.getItem(i);
        if (thisItem != null) {
          thisItem = thisItem.clone();
          boolean isAir = thisItem.getType() == Material.AIR;

          boolean lastItem = thisItem.getAmount() == 1 && !RompPlugin.getInstance().getConfig().getBoolean("enableRecipeEmpty");

          if (lastItem && !isAir && pos != 0) {
            event.setCancelled(true);
            return;
          }

          thisItem.setAmount(1);
          stack[pos] = thisItem;
          stackList.add(thisItem);
        } else {
          stack[pos] = null;
        }

        pos++;
      }

      RecipeShape shape = new RecipeShape(stack);
      ShapedRecipe shapedCrafted = RompPlugin.getInstance().getShapedRecipeMap().get(shape);
      ShapelessRecipe shapelessCrafted = RompPlugin.getInstance().getShapelessRecipeMap().get(stackList);

      ItemStack outItem = null;
      if (shapelessCrafted != null) outItem = shapelessCrafted.getResult();
      if (shapedCrafted != null) outItem = shapedCrafted.getResult();

      if (outItem != null) {
        event.setItem(outItem);

        for (int i : allRows) {
          ItemStack usedStack = sourceInventory.getItem(i);
          if (usedStack != null) usedStack.setAmount(usedStack.getAmount() - 1);
          sourceInventory.setItem(i, usedStack);
        }
      } else {
        event.setCancelled(true);
      }
    }
  }

  private boolean contains(final int[] array, final int key) {
    for (int i : array) {
      if (i == key) {
        return true;
      }
    }
    return false;
  }
}

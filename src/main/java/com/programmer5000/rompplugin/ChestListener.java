package com.programmer5000.rompplugin;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;

class RecipeShape {
  private ItemStack[] items;
  public RecipeShape(ItemStack[] items) {
    this.items = items;
  }
  public int hashCode() {
    return Objects.hash(items);
  }
  public boolean equals(Object o) {
    return o != null && o.hashCode() == hashCode();
  }
}

public class ChestListener implements Listener {
//  InventoryInteractEvent
//  @EventHandler
//  public void onPlayerJoin(PlayerJoinEvent event) {
//    Bukkit.broadcastMessage("Welcome to the server!");
//  }

//  @EventHandler
//  public void onInventoryInteract(InventoryInteractEvent event) {
//    Bukkit.getLogger().info("inventory interact!!!");
//  }

  final int[] topRow = {0, 1, 2};
  final int[] middleRow = {9, 10, 11};
  final int[] bottomRow = {18, 19, 20};
  final int[] allRows = {0, 1, 2, 9, 10, 11, 18, 19, 20};


  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(final PlayerJoinEvent event){
    StatsBoard board = PlayerDataManager.getScorebaord(event.getPlayer());
    if(board != null){
      board.addPlayer(event.getPlayer());
    }else{
      StatsBoard.clearPlayer(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInventoryClick(final InventoryClickEvent event){
//    Bukkit.getLogger().info("inventory click event");
    boolean isChest = event.getInventory().getType() == InventoryType.CHEST;
    if(!isChest) return;
    if(!checkInventoryIsSpecialChest(event.getInventory())) return;

    boolean slotIsContainer = event.getSlotType() == InventoryType.SlotType.CONTAINER;
    boolean clickedChest = (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CHEST);

    boolean otherInventoryMove = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.COLLECT_TO_CURSOR;

    int slot = event.getSlot();
    boolean isOkSlot = contains(allRows, slot);

    if (isChest && ((slotIsContainer && clickedChest && !isOkSlot) || otherInventoryMove)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamage(final EntityDamageEvent event){
    if(event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();

      PlayerDataManager.addFallHeight(player, (int) Math.floor((event.getDamage()) + 3));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryDrag(final InventoryDragEvent event){
//    Bukkit.getLogger().info("inventory drag event");
    boolean isChest = event.getInventory().getType() == InventoryType.CHEST;
    if(!isChest) return;
    if(!checkInventoryIsSpecialChest(event.getInventory())) return;

    boolean clickedChest = event.getInventory() != null && event.getInventory().getType() == InventoryType.CHEST;
    Set<Integer> slots = event.getInventorySlots();
    boolean isOkSlot = true;

    for(Integer i : slots){
      if (!contains(allRows, i)) {
        isOkSlot = false;
        break;
      }
    }

    if ((isChest && clickedChest) && !isOkSlot) {
      event.setCancelled(true);
    }
  }

  private boolean checkInventoryIsSpecialChest(Inventory inventory){
    try {
      return inventory.getType() == InventoryType.CHEST && !(inventory instanceof DoubleChest) && ((Chest) inventory.getHolder()).getBlock().getType() == Material.TRAPPED_CHEST;
    }catch(java.lang.ClassCastException ignored){
      return false;
    }
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryMove(final InventoryMoveItemEvent event){
//    Bukkit.getLogger().info("inventory move event");
    Inventory sourceInventory = event.getSource();
    Inventory destInventory = event.getDestination();

    boolean originatesFromChest = sourceInventory.getType() == InventoryType.CHEST;
    boolean goesToChest = destInventory.getType() == InventoryType.CHEST;

    boolean originatesFromHopper = sourceInventory.getType() == InventoryType.HOPPER;
    boolean goesToHopper = destInventory.getType() == InventoryType.HOPPER;

    boolean chestToHopper = originatesFromChest && goesToHopper;
    boolean hopperToChest = originatesFromHopper && goesToChest;

    if(!chestToHopper && !hopperToChest) return;

    if(hopperToChest) {
//      Bukkit.getLogger().info("hopper to chest");
      if(!checkInventoryIsSpecialChest(destInventory)) return;

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
    if(chestToHopper){
      if(!checkInventoryIsSpecialChest(sourceInventory)) return;

      ItemStack[] stack = new ItemStack[9];
      ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();

      int pos = 0;
      for(int i : allRows){
        ItemStack thisItem = sourceInventory.getItem(i);
        if(thisItem != null) {
          thisItem = thisItem.clone();
          boolean isAir = thisItem.getType() == Material.AIR;

          boolean lastItem = thisItem.getAmount() == 1 && !SpigotPlugin.getInstance().getConfig().getBoolean("enableRecipeEmpty");

          if (lastItem && !isAir && pos != 0) {
            event.setCancelled(true);
            return;
          }

          thisItem.setAmount(1);
          stack[pos] = thisItem;
          stackList.add(thisItem);
        }else {
          stack[pos] = null;
        }

        pos++;
      }

      RecipeShape shape = new RecipeShape(stack);
      ShapedRecipe shapedCrafted = SpigotPlugin.getInstance().getShapedRecipeMap().get(shape);
      ShapelessRecipe shapelessCrafted = SpigotPlugin.getInstance().getShapelessRecipeMap().get(stackList);

      ItemStack outItem = null;
      if(shapelessCrafted != null) outItem = shapelessCrafted.getResult();
      if(shapedCrafted != null) outItem = shapedCrafted.getResult();

      if(outItem != null) {
        event.setItem(outItem);

        for(int i : allRows){
          ItemStack usedStack = sourceInventory.getItem(i);
          if(usedStack != null) usedStack.setAmount(usedStack.getAmount() - 1);
          sourceInventory.setItem(i, usedStack);
        }
      }else{
        event.setCancelled(true);
      }
    }

//    Bukkit.getLogger().info("number of viewers: " + sourceInventory.getViewers().size() + " " + destInventory.getViewers().size());
//    sourceInventory.getViewers().forEach(humanEntity -> {
//      if(humanEntity instanceof Player){
//        Bukkit.getLogger().info("updating inventory1 for player " + humanEntity.getUniqueId());
//        ((Player) humanEntity).updateInventory();
//      }
//    });
//    destInventory.getViewers().forEach(humanEntity -> {
//      if(humanEntity instanceof Player){
//        Bukkit.getLogger().info("updating inventory2 for player " + humanEntity.getUniqueId());
//        ((Player) humanEntity).updateInventory();
//      }
//    });
  }

  private boolean contains(final int[] array, final int key){
    for(int i : array){
      if(i == key){
        return true;
      }
    }
    return false;
  }
}

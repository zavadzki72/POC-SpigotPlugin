package com.marccusz.inventories;

import com.marccusz.Data.Repositories.AutoSoupConfigurationRepository;
import com.marccusz.MM_Back;
import com.marccusz.Models.AutoSoupConfigurationEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class AutoSoupConfigurationInventory implements Listener
{
    private final Inventory _inv;
    private final AutoSoupConfigurationEntity _autoSoupConfiguration;

    private ItemStack _closeItem;
    private final List<Integer> _autoSoupChangeSlots = Arrays.asList(11, 20);
    private final List<Integer> _quickDropChangeSlots = Arrays.asList(13, 22);
    private final List<Integer> _autoRefillChangeSlots = Arrays.asList(15, 24);

    public AutoSoupConfigurationInventory(MM_Back plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        _inv = Bukkit.createInventory(null, 45, "Autosoup Configuration");
        _autoSoupConfiguration = null;
    }

    public AutoSoupConfigurationInventory(MM_Back plugin, AutoSoupConfigurationEntity autoSoupConfiguration)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        _inv = Bukkit.createInventory(null, 45, "Autosoup Configuration");
        _autoSoupConfiguration = autoSoupConfiguration;

        initializeItems();
    }

    public void initializeItems()
    {
        ItemStack autoSoupConfigItem = createGuiItem(Material.MUSHROOM_SOUP, 0, "§aUse autosoup", "§7Click for turn on the auto soup");
        ItemStack quickDropConfigItem = createGuiItem(Material.BOWL, 0, "§aUse quickdrop", "§7Click for turn on the quick drop", "§cThe autosoup configuration must be true");
        ItemStack refillConfigItem = createGuiItem(Material.CHEST, 0, "§aUse auto refill", "§7Click for turn on the automatic refill", "§cThe autosoup configuration must be true");

        _inv.setItem(_autoSoupChangeSlots.get(0), autoSoupConfigItem);
        _inv.setItem(_quickDropChangeSlots.get(0), quickDropConfigItem);
        _inv.setItem(_autoRefillChangeSlots.get(0), refillConfigItem);

        ItemStack configOnItem = createGuiItem(Material.INK_SACK, 10, "§cTurn off", "§7Click for turn off");
        ItemStack configOffItem = createGuiItem(Material.INK_SACK, 8, "§aTurn on", "§7Click for turn on");

        _inv.setItem(_autoSoupChangeSlots.get(1), (_autoSoupConfiguration.AutoSoupOn) ? configOnItem : configOffItem);
        _inv.setItem(_quickDropChangeSlots.get(1), (_autoSoupConfiguration.QuickDropOn) ? configOnItem : configOffItem);
        _inv.setItem(_autoRefillChangeSlots.get(1), (_autoSoupConfiguration.AutoRefillOn) ? configOnItem : configOffItem);

        _closeItem = createGuiItem(Material.INK_SACK, 1, "§cClose");
        _inv.setItem(40, _closeItem);
    }

    protected ItemStack createGuiItem(Material material, int materialMetaData, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1, (byte)materialMetaData);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        if(lore.length > 0){
            meta.setLore(Arrays.asList(lore));
        }

        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(Player player) {
        player.openInventory(_inv);
    }
    public void closeInventory(Player player) {
        player.closeInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getInventory().equals(_inv)){
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if(clickedItem.isSimilar(_closeItem)){
            closeInventory(player);
            return;
        }

        int slotClicked = event.getSlot();
        boolean isChanged = false;

        if(_autoSoupChangeSlots.contains(slotClicked))
        {
            _autoSoupConfiguration.ChangeAutoSoup();
            isChanged = true;
        }

        if(_quickDropChangeSlots.contains(slotClicked))
        {
            _autoSoupConfiguration.ChangeQuickDrop();
            isChanged = true;
        }

        if(_autoRefillChangeSlots.contains(slotClicked))
        {
            _autoSoupConfiguration.ChangeAutoRefill();
            isChanged = true;
        }

        if(isChanged)
        {
            AutoSoupConfigurationRepository.Update(_autoSoupConfiguration);
            player.sendMessage("§aSettings updated successfully!");
            closeInventory(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent event) {
        if (!event.getInventory().equals(_inv)){
            return;
        }

        event.setCancelled(true);
    }
}

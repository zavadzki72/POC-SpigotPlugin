package com.marccusz.listeners;

import com.marccusz.Data.Repositories.AutoSoupConfigurationRepository;
import com.marccusz.Data.Repositories.PlayerRepository;
import com.marccusz.MM_Back;
import com.marccusz.Models.AutoSoupConfigurationEntity;
import com.marccusz.Models.PlayerEntity;
import com.marccusz.utils.PlayerUtils;

import com.marccusz.utils.SoupUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class PlayerEvents implements Listener {

    private final MM_Back _plugin;

    public PlayerEvents(MM_Back plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        _plugin = plugin;
    }

    @EventHandler
    public void OnClickSoup(PlayerInteractEvent event){

        List<Action> actionsToEvent = Arrays.asList(
                Action.RIGHT_CLICK_AIR,
                Action.RIGHT_CLICK_BLOCK
        );

        if(!actionsToEvent.contains(event.getAction())){
            return;
        }

        if(event.getItem() == null || event.getItem().getType() != Material.MUSHROOM_SOUP){
            return;
        }

        if(event.getPlayer().getHealth() == event.getPlayer().getMaxHealth()){
            return;
        }

        double soupHealth = PlayerUtils.GetHealthByHearts(3);
        double playerLifeWithSoup = event.getPlayer().getHealth() + soupHealth;

        if(playerLifeWithSoup > event.getPlayer().getMaxHealth()){
            playerLifeWithSoup = event.getPlayer().getMaxHealth();
        }

        event.getPlayer().setHealth(playerLifeWithSoup);
        event.getPlayer().getItemInHand().setType(Material.BOWL);
    }

    @EventHandler
    public void OnDamageAutoSoup(EntityDamageEvent event) {

        if(!(event.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerEntity playerEntity = PlayerRepository.GetByUuid(player.getUniqueId());
        AutoSoupConfigurationEntity autoSoupConfiguration = AutoSoupConfigurationRepository.GetByPlayerId(playerEntity.Id);

        boolean autoSoup = autoSoupConfiguration.AutoSoupOn;
        boolean quickDrop = autoSoupConfiguration.QuickDropOn;
        boolean autoRefill = autoSoupConfiguration.AutoRefillOn;

        if(!autoSoup){
            return;
        }

        double minHealthToSoup =  PlayerUtils.GetHealthByHearts(4);

        if(player.getHealth() > minHealthToSoup){
            return;
        }

        if(!player.getInventory().contains(Material.MUSHROOM_SOUP)){
            return;
        }

        int soupSlot = player.getInventory().first(Material.MUSHROOM_SOUP);
        int actualSlot = player.getInventory().getHeldItemSlot();

        if(soupSlot > 8){

            if(!autoRefill){
                return;
            }

            SoupUtils.MakeRefill(player);
            soupSlot = player.getInventory().first(Material.MUSHROOM_SOUP);
        }

        if(soupSlot >= 0 && soupSlot <= 8){
            player.getInventory().setHeldItemSlot(soupSlot);
        }
        else {
            return;
        }

        Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, new ItemStack(Material.MUSHROOM_SOUP), player.getLocation().getBlock(), player.getLocation().getBlock().getFace(player.getLocation().getBlock())));

        if(quickDrop && player.getItemInHand() != null){
            if(player.getItemInHand().getType() == Material.BOWL){
                ItemStack itemToDrop = player.getItemInHand();
                player.setItemInHand(null);

                Vector vector = new Vector(0, 2, 0);
                Location loc = player.getLocation().add(vector);

                player.getWorld().dropItem(loc, itemToDrop).setVelocity(player.getEyeLocation().getDirection().multiply(0.25));
            }
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                player.getInventory().setHeldItemSlot(actualSlot);
            }
        }.runTaskLater(_plugin, 5);
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {

        Player playerEvent = event.getPlayer();
        PlayerEntity playerEntity = PlayerRepository.GetByUuid(playerEvent.getUniqueId());

        if(playerEntity == null){
            playerEvent.sendMessage("§6Welcome to Server! It's your first time here =)");
            playerEvent.sendMessage("§6Please waiting while we preparing your data.");

            playerEntity = new PlayerEntity(playerEvent.getUniqueId().toString(), playerEvent.getName());
            Integer playerInsertedId = PlayerRepository.Insert(playerEntity);

            if(playerInsertedId != null){
                AutoSoupConfigurationEntity autoSoupConfiguration = new AutoSoupConfigurationEntity(playerInsertedId, false, false, false);
                AutoSoupConfigurationRepository.Insert(autoSoupConfiguration);
            }

            playerEvent.sendMessage("§cAll good! Thanks for waiting, good game for you!");
            return;
        }

        playerEntity.OnLogin();
        PlayerRepository.Update(playerEntity);

        playerEvent.sendMessage("§2Welcome back!");
    }

}

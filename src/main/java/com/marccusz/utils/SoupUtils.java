package com.marccusz.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SoupUtils {

    public static void MakeRefill(Player player){
        ArrayList<Integer> freeSlotsToPutSoup = new ArrayList<>();
        ArrayList<Integer> slotsWithSoup = new ArrayList<>();

        for (int slot = 0; slot < player.getInventory().getSize(); slot++){

            if(slot <= 8 && player.getInventory().getItem(slot) == null){
                freeSlotsToPutSoup.add(slot);
            }else if(slot >= 9 && player.getInventory().getItem(slot) != null){
                if(player.getInventory().getItem(slot).getType() == Material.MUSHROOM_SOUP){
                    slotsWithSoup.add(slot);
                }
            }

        }

        if(freeSlotsToPutSoup.isEmpty() || slotsWithSoup.isEmpty()){
            return;
        }

        for (Integer integer : freeSlotsToPutSoup) {
            if (!slotsWithSoup.isEmpty()) {
                player.getInventory().setItem(integer, new ItemStack(Material.MUSHROOM_SOUP));

                int indexToRemove = slotsWithSoup.size() - 1;
                player.getInventory().setItem(slotsWithSoup.get(indexToRemove), new ItemStack(Material.AIR));
                slotsWithSoup.remove(indexToRemove);
            }
        }

        slotsWithSoup.clear();
        freeSlotsToPutSoup.clear();
    }

}

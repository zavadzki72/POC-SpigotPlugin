package com.marccusz.commands;

import com.marccusz.Data.Repositories.AutoSoupConfigurationRepository;
import com.marccusz.Data.Repositories.PlayerRepository;
import com.marccusz.MM_Back;
import com.marccusz.Models.AutoSoupConfigurationEntity;
import com.marccusz.Models.PlayerEntity;
import com.marccusz.inventories.AutoSoupConfigurationInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoSoupCommand implements CommandExecutor
{
    private final MM_Back _plugin;

    public AutoSoupCommand(MM_Back plugin){
        plugin.getCommand("autosoup").setExecutor(this);
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String aliases, String[] strings)
    {
        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player)commandSender;

        PlayerEntity playerEntity = PlayerRepository.GetByUuid(player.getUniqueId());
        AutoSoupConfigurationEntity autoSoupConfiguration = AutoSoupConfigurationRepository.GetByPlayerId(playerEntity.Id);

        AutoSoupConfigurationInventory inv = new AutoSoupConfigurationInventory(_plugin, autoSoupConfiguration);
        inv.openInventory(player);

        return true;
    }
}

package com.marccusz;

import com.google.common.cache.LoadingCache;
import com.marccusz.Data.DataConnectionManager;
import com.marccusz.Models.PlayerEntity;
import com.marccusz.commands.AutoSoupCommand;
import com.marccusz.inventories.AutoSoupConfigurationInventory;
import com.marccusz.listeners.PlayerEvents;
import com.marccusz.utils.InitialTablesDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MM_Back extends JavaPlugin {

    @Override
    public void onEnable() {

        CreateDatabase();

        RegisterEvents();
        RegisterCommands();
        RegisterInventories();

        Bukkit.getConsoleSender().sendMessage("§6 Plugin on!");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§4 Plugin off!");
    }

    private void CreateDatabase(){
        DataConnectionManager.SetConnection();
        InitialTablesDatabaseUtil.PrepareDatabaseToPlugin();
    }

    private void RegisterEvents(){
        new PlayerEvents(this);
    }

    private void RegisterCommands(){
        new AutoSoupCommand(this);
    }

    private void RegisterInventories(){
        new AutoSoupConfigurationInventory(this);
    }
}

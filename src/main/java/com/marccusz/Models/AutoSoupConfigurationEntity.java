package com.marccusz.Models;

public class AutoSoupConfigurationEntity
{
    public static String TableName = "AutoSoupConfiguration";
    public static String IdColunmName = "Id";
    public static String PlayerIdColunmName = "PlayerId";
    public static String AutoSoupOnColunmName = "AutoSoupOn";
    public static String QuickDropOnColunmName = "QuickDropOn";
    public static String AutoRefillOnColunmName = "AutoRefillOn";

    public int Id;
    public int PlayerId;
    public boolean AutoSoupOn;
    public boolean QuickDropOn;
    public boolean AutoRefillOn;

    public AutoSoupConfigurationEntity(int id, int playerId, boolean autoSoupOn, boolean quickDropOn, boolean autoRefillOn)
    {
        Id = id;
        PlayerId = playerId;
        AutoSoupOn = autoSoupOn;
        QuickDropOn = quickDropOn;
        AutoRefillOn = autoRefillOn;
    }

    public AutoSoupConfigurationEntity(int playerId, boolean autoSoupOn, boolean quickDropOn, boolean autoRefillOn)
    {
        PlayerId = playerId;
        AutoSoupOn = autoSoupOn;
        QuickDropOn = quickDropOn;
        AutoRefillOn = autoRefillOn;
    }

    public void SetId(int id){
        Id = id;
    }

    public void ChangeAutoSoup(){
        AutoSoupOn = !AutoSoupOn;
    }

    public void ChangeQuickDrop(){
        QuickDropOn = !QuickDropOn;
    }

    public void ChangeAutoRefill(){
        AutoRefillOn = !AutoRefillOn;
    }
}

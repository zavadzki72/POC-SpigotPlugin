package com.marccusz.Models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PlayerEntity
{
    public static String TableName = "Player";
    public static String IdColunmName = "Id";
    public static String BukkitIdColunmName = "BukkitId";
    public static String NickNameColunmName = "NickName";
    public static String CreatedAtColunmName = "CreatedAt";
    public static String LastLoginColunmName = "LastLogin";

    public PlayerEntity(int id, String bukkitId, String nickName, Timestamp createdAt, Timestamp lastLogin){
        Id = id;
        NickName = nickName;
        CreatedAt = createdAt;
        LastLogin = lastLogin;

        BukkitId = UUID.fromString(bukkitId);
    }

    public PlayerEntity(String bukkitId, String nickName){
        NickName = nickName;

        Timestamp date = new Timestamp(new Date().getTime());

        CreatedAt = date;
        LastLogin = date;

        BukkitId = UUID.fromString(bukkitId);
    }

    public int Id;
    public UUID BukkitId;
    public String NickName;
    public Timestamp CreatedAt;
    public Timestamp LastLogin;

    public void SetId(int id){
        Id = id;
    }
    public void OnLogin(){
        LastLogin = new Timestamp(new Date().getTime());
    }
}

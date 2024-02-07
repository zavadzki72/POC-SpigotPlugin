package com.marccusz.Data.Repositories;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.marccusz.Data.DataConnectionManager;
import com.marccusz.Models.AutoSoupConfigurationEntity;
import com.marccusz.Models.PlayerEntity;

import java.sql.*;
import java.util.UUID;

public class PlayerRepository
{
    static LoadingCache<String, PlayerEntity> playerCache = CacheBuilder.newBuilder().build(new CacheLoader<String, PlayerEntity>()
    {
        @Override
        public PlayerEntity load(String key)
        {
            String uuidStr = key.split("_")[2];
            return GetByUuidFromDb(UUID.fromString(uuidStr), key);
        }
    });

    public static PlayerEntity GetByUuid(UUID uuid)
    {
        String cacheKey = "PLAYER_CACHE_" + uuid;

        PlayerEntity response = null;

        try
        {
            response = playerCache.get(cacheKey);
        }
        catch (Exception ex)
        {
            System.out.println("Error on get item in cache, cache key ("+ cacheKey +"), Ex Details: " + ex.getMessage());
        }

        return response;
    }

    public static PlayerEntity GetByUuidFromDb(UUID uuid, String cacheKey)
    {
        String getByUuidQuery = "SELECT * from "+ PlayerEntity.TableName +" where "+ PlayerEntity.BukkitIdColunmName +" = '" + uuid +"'";
        PlayerEntity playerResponse = null;

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getByUuidQuery);

            if (resultSet.next()){
                playerResponse = ParseResultSetOnPlayerEntity(resultSet);
            }

            if(playerResponse != null){
                playerCache.put(cacheKey, playerResponse);
            }

            resultSet.close();
            statement.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute select query ("+ getByUuidQuery +"), Ex Details: " + ex.getMessage());
        }

        return playerResponse;
    }

    public static Integer Insert(PlayerEntity player)
    {
        String insertQuery = "INSERT INTO "+ PlayerEntity.TableName +"("+ PlayerEntity.BukkitIdColunmName +", "+ PlayerEntity.NickNameColunmName +", "+ PlayerEntity.CreatedAtColunmName +", "+ PlayerEntity.LastLoginColunmName +")" +
                " VALUES('"+ player.BukkitId +"', '"+ player.NickName +"', '"+ player.CreatedAt +"', '"+ player.LastLogin +"')";
        Integer idOfInsert = null;

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            prepareStatement.executeUpdate();

            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                idOfInsert = generatedKeys.getInt(1);

                player.SetId(idOfInsert);
                String cacheKey = "PLAYER_CACHE_" + player.BukkitId;
                playerCache.put(cacheKey, player);
            }

            prepareStatement.close();

            return idOfInsert;
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute insert query ("+ insertQuery +"), Ex Details: " + ex.getMessage());
            return null;
        }
    }

    public static void Update(PlayerEntity player)
    {
        String insertQuery = "UPDATE "+ PlayerEntity.TableName +" SET "+
                PlayerEntity.NickNameColunmName +" = '"+ player.NickName +"', " +
                PlayerEntity.LastLoginColunmName +" = '"+ player.LastLogin +"' " +
                "WHERE "+ PlayerEntity.IdColunmName+"="+player.Id+";";

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            Statement statement = connection.createStatement();

            int rows = statement.executeUpdate(insertQuery);
            if(rows > 0)
            {
                String cacheKey = "PLAYER_CACHE_" + player.BukkitId;
                playerCache.put(cacheKey, player);
            }

            statement.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute update query ("+ insertQuery +"), Ex Details: " + ex.getMessage());
        }
    }

    private static PlayerEntity ParseResultSetOnPlayerEntity(ResultSet resultSet) throws SQLException
    {
        int id = resultSet.getInt(PlayerEntity.IdColunmName);
        String bukkitId = resultSet.getString(PlayerEntity.BukkitIdColunmName);
        String nickName = resultSet.getString(PlayerEntity.NickNameColunmName);
        Timestamp createdAt = resultSet.getTimestamp(PlayerEntity.CreatedAtColunmName);
        Timestamp lastLogin = resultSet.getTimestamp(PlayerEntity.LastLoginColunmName);

        return new PlayerEntity(id, bukkitId, nickName, createdAt, lastLogin);
    }
}

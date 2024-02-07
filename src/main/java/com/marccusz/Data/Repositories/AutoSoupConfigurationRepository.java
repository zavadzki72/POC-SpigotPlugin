package com.marccusz.Data.Repositories;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.marccusz.Data.DataConnectionManager;
import com.marccusz.Models.AutoSoupConfigurationEntity;

import java.sql.*;

public class AutoSoupConfigurationRepository
{
    static LoadingCache<String, AutoSoupConfigurationEntity> autoSoupConfigurationCache = CacheBuilder.newBuilder().build(new CacheLoader<String, AutoSoupConfigurationEntity>()
    {
        @Override
        public AutoSoupConfigurationEntity load(String key)
        {
            String playerId = key.split("_")[2];
            return GetByPlayerIdFromDb(Integer.parseInt(playerId), key);
        }
    });

    public static AutoSoupConfigurationEntity GetByPlayerId(int playerId)
    {
        String cacheKey = "AUTOSOUPCONFIGURATION_CACHE_" + playerId;

        AutoSoupConfigurationEntity response = null;

        try
        {
            response = autoSoupConfigurationCache.get(cacheKey);
        }
        catch (Exception ex)
        {
            System.out.println("Error on get item in cache, cache key ("+ cacheKey +"), Ex Details: " + ex.getMessage());
        }

        return response;
    }

    public static AutoSoupConfigurationEntity GetByPlayerIdFromDb(int playerId, String cacheKey)
    {

        String getByPlayerIdQuery = "SELECT * from "+ AutoSoupConfigurationEntity.TableName +" where "+ AutoSoupConfigurationEntity.PlayerIdColunmName +" = " + playerId + ";";
        AutoSoupConfigurationEntity response = null;

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getByPlayerIdQuery);

            if (resultSet.next()){
                response = ParseResultSetOnAutoSoupConfigurationEntity(resultSet);
            }

            if(response != null){
                autoSoupConfigurationCache.put(cacheKey, response);
            }

            resultSet.close();
            statement.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute select query ("+ getByPlayerIdQuery +"), Ex Details: " + ex.getMessage());
        }

        return response;
    }

    public static Integer Insert(AutoSoupConfigurationEntity autoSoupConfiguration)
    {
        String insertQuery = "INSERT INTO "+ AutoSoupConfigurationEntity.TableName +"("+ AutoSoupConfigurationEntity.PlayerIdColunmName +", "+ AutoSoupConfigurationEntity.AutoSoupOnColunmName +", "+ AutoSoupConfigurationEntity.QuickDropOnColunmName +", "+ AutoSoupConfigurationEntity.AutoRefillOnColunmName +")" +
                " VALUES('"+ autoSoupConfiguration.PlayerId +"', "+ autoSoupConfiguration.AutoSoupOn +", "+ autoSoupConfiguration.QuickDropOn +", "+ autoSoupConfiguration.AutoRefillOn +")";
        Integer idOfInsert = null;

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            prepareStatement.executeUpdate();

            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                idOfInsert = generatedKeys.getInt(1);

                autoSoupConfiguration.SetId(idOfInsert);
                String cacheKey = "AUTOSOUPCONFIGURATION_CACHE_" + autoSoupConfiguration.PlayerId;
                autoSoupConfigurationCache.put(cacheKey, autoSoupConfiguration);
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

    public static void Update(AutoSoupConfigurationEntity autoSoupConfiguration)
    {
        String insertQuery = "UPDATE "+ AutoSoupConfigurationEntity.TableName +" SET "+
                AutoSoupConfigurationEntity.AutoSoupOnColunmName +" = "+ autoSoupConfiguration.AutoSoupOn +", " +
                AutoSoupConfigurationEntity.QuickDropOnColunmName +" = "+ autoSoupConfiguration.QuickDropOn +", " +
                AutoSoupConfigurationEntity.AutoRefillOnColunmName +" = "+ autoSoupConfiguration.AutoRefillOn + " " +
                "WHERE "+AutoSoupConfigurationEntity.IdColunmName+"="+autoSoupConfiguration.Id+";";

        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            Statement statement = connection.createStatement();

            int rows = statement.executeUpdate(insertQuery);
            if(rows > 0)
            {
                String cacheKey = "AUTOSOUPCONFIGURATION_CACHE_" + autoSoupConfiguration.PlayerId;
                autoSoupConfigurationCache.put(cacheKey, autoSoupConfiguration);
            }

            statement.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute update query ("+ insertQuery +"), Ex Details: " + ex.getMessage());
        }
    }

    private static AutoSoupConfigurationEntity ParseResultSetOnAutoSoupConfigurationEntity(ResultSet resultSet) throws SQLException
    {
        int id = resultSet.getInt(AutoSoupConfigurationEntity.IdColunmName);
        int playerId = resultSet.getInt(AutoSoupConfigurationEntity.PlayerIdColunmName);
        boolean autoSoupOn = resultSet.getBoolean(AutoSoupConfigurationEntity.AutoSoupOnColunmName);
        boolean quickDropOn = resultSet.getBoolean(AutoSoupConfigurationEntity.QuickDropOnColunmName);
        boolean autoRefillOn = resultSet.getBoolean(AutoSoupConfigurationEntity.AutoRefillOnColunmName);

        return new AutoSoupConfigurationEntity(id, playerId, autoSoupOn, quickDropOn, autoRefillOn);
    }
}

package com.marccusz.utils;

import com.marccusz.Data.DataConnectionManager;

import java.io.Console;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class InitialTablesDatabaseUtil
{
    public static String _createPlayerTable = "CREATE TABLE IF NOT EXISTS Player(" +
            "    Id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
            "    BukkitId VARCHAR(50) NOT NULL," +
            "    Nickname VARCHAR(50) NOT NULL," +
            "    CreatedAt DATETIME NOT NULL," +
            "    LastLogin DATETIME NOT NULL," +
            "    UNIQUE(BukkitId)" +
    ");";

    public static String _createAutSoupConfigurationTable = "CREATE TABLE IF NOT EXISTS AutoSoupConfiguration(" +
            "    Id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
            "    PlayerId INT NOT NULL," +
            "    AutoSoupOn BOOLEAN NOT NULL," +
            "    QuickDropOn BOOLEAN NOT NULL," +
            "    AutoRefillOn BOOLEAN NOT NULL," +
            "    UNIQUE(PlayerId)," +
            "    FOREIGN KEY(PlayerId) REFERENCES Player(Id)" +
    ");";

    public static List<String> tables = Arrays.asList(_createPlayerTable, _createAutSoupConfigurationTable);

    public static void PrepareDatabaseToPlugin(){
        for (String queryTable : tables){
            CreateTable(queryTable);
        }
    }

    private static void CreateTable(String query){
        try
        {
            Connection connection = DataConnectionManager.GetConnection();
            Statement statement = connection.createStatement();

            statement.execute(query);

            statement.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error on execute create initial tables on query ("+ query +"), Ex Details: " + ex.getMessage());
        }
    }
}

package com.marccusz.Data;

import java.sql.*;

public class DataConnectionManager
{
    public static Connection _connection = null;

    public static String _hostDb = "127.0.0.1";
    public static String _userDb = "root";
    public static String _passwordDb = "root";
    public static String _nameDb = "MM_BACK";

    private static Connection CreateConnection() throws Exception
    {
        String connString = "jdbc:mysql://" + _hostDb + "/" + _nameDb;

        try
        {
            return DriverManager.getConnection(connString, _userDb, _passwordDb);
        }
        catch (Exception ex)
        {
            System.out.println("Error on connect to database: (" + connString + "). Ex details: " + ex.getMessage());
            throw ex;
        }
    }

    public static Connection GetConnection() throws Exception
    {
        if(_connection != null)
        {
            return _connection;
        }

        return CreateConnection();
    }

    public static void SetConnection()
    {

        if(_connection != null)
        {
            return;
        }

        try
        {
            _connection = CreateConnection();
        }
        catch (Exception ex)
        {
            System.out.println("Unable to set connection");
        }
    }

    public static void CloseConnection() throws SQLException
    {
        if(_connection == null)
        {
            return;
        }

        _connection.close();
        _connection = null;
    }
}

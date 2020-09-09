package weka;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class testWeka {

    //  Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3307/db_test1";
    static final String USER = "admin";
    static final String PASS = "admin";

    public static void main(String[] argv) {

        System.out.println("Testing connection to mySQL JDBC");

        try {
            Class.forName("com.mysql.jdbc.Driver");
/*
                Class.forName(“полное имя класса”)
                Class.forName(“полное имя класса”).newInstance()
                DriverManager.registerDriver(new “полное имя класса”)
*/
        } catch (ClassNotFoundException e) {
            System.out.println("mySQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("mySQL JDBC Driver successfully connected");
        Connection connection = null;

        try {

            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);


        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }

}

//STEP 1. Import required packages

import java.sql.*;

public class dbSourse {

    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/treeDatabase";
    static final String USER = "postgres";
    static final String PASS = "admin";

    public static void main(String[] argv) {

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);


            String result = "";
            CallableStatement callableStatement =
                    connection.prepareCall("{? = call pivotcode2(?,?,?,?,?)}");
            //  CallableStatement callableStatement = connection.prepareCall(runFunction);

            //----------------------------------

            // output
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // input
            callableStatement.setString(2, "public.view03");
            callableStatement.setString(3, "record_id");
            callableStatement.setString(4, "namefields");
            callableStatement.setString(5, "fieldvalue");
            callableStatement.setString(6, "type_collum");

            // Run hello() function
            callableStatement.execute();

            // Get result
            result = callableStatement.getString(1);
            System.out.println(result);

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




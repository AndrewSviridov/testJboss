
//STEP 1. Import required packages

import myDBWeka.myDB_InstanceQuery;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.sql.*;

public class treeDatabase {
    Connection connection;

    public treeDatabase(Connection con) {
        connection = con;
    }

/*
    public String getRule() {
        try {

            String SELECT_QUERY = "SELECT \n" +
                    "  fields_ik.\"idFields\",\n" +
                    "  fields_ik.idprecedent\n" +
                    "FROM\n" +
                    "  fields_ik\n";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_QUERY);

            while (resultSet.next()) {
                Array ar = resultSet.getArray(2);
                System.out.println(resultSet.getString(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }




    public String getData() {
        try {

            String SELECT_QUERY = "SELECT \n" +
                    "  public.value_s.\"idValue_s\",\n" +
                    "  public.value_s.\"Fields_IK_idFields\",\n" +
                    "  public.value_s.fieldvalue,\n" +
                    "  public.fields_ik.\"idFields\",\n" +
                    "  public.fields_ik.\"NameFields\",\n" +
                    "  public.fields_ik.\"TitlesTablesFields\",\n" +
                    "  public.fields_ik.\"UUIDFields\",\n" +
                    "  public.fields_ik.idprecedent\n" +
                    "FROM\n" +
                    "  public.value_s\n" +
                    "  INNER JOIN public.fields_ik ON (public.value_s.\"Fields_IK_idFields\" = public.fields_ik.\"idFields\")";
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SELECT_QUERY);

            while (resultSet.next()) {

                Array ar = resultSet.getArray(2);
                System.out.println(resultSet.getString(2));



            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";

    }
*/

    public String getFunctionData() {
        String result = "";
        try {
            //String runFunction = "?=call pivotcode2('?','?', '?','?','varchar') ";
            //String sql = "select pivotcode2('?','?', '?','?','varchar')";
            //CallableStatement cstmt = connection.prepareCall("{call setGoodsData(?, ?)}");
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

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(result);


            while (resultSet.next()) {

                Array ar = resultSet.getArray(2);
                System.out.println(resultSet.getString(2));


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void myTest() {
        try {

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


            myDB_InstanceQuery query = new myDB_InstanceQuery();
            query.setM_Connection(connection);
            query.setCustomPropsFile(new File("C:\\Users\\Andrew\\wekafiles\\props\\DatabaseUtils.props.postgresql"));
            query.setQuery(result);

            Instances data = query.retrieveInstances();


          /*  Instance inst;
            int count = 0;
            while ((inst = loader.getNextInstance(structure)) != null) {
                data.add(inst);
                count++;
                if ((count % 100) == 0)
                    System.out.println(count + " rows read so far.");
            }*/
            System.out.println(data.toSummaryString());
            //System.out.println(data);
            //set class index to the last attribute
            data.setClassIndex(data.numAttributes() - 1);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


/*
    PreparedStatement preparedStatement =
            connection.prepareStatement(sql);
preparedStatement.setString(1, "Gary");
preparedStatement.setString(2, "Larson");
preparedStatement.setLong(3, 123);
*/



    /**/


/*
    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://192.168.0.105:5432/treeDatabase";
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
    */

}
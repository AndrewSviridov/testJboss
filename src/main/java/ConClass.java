import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;
import weka.core.converters.DatabaseLoader;
import weka.experiment.InstanceQuery;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class ConClass {

    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://192.168.0.105:5432/treeDatabase";
    static final String USER = "postgres";
    static final String PASS = "admin";


    public static void main(String[] argv) {

        System.out.println("Testing connection to postgresql JDBC");

        try {
            Class.forName("org.postgresql.Driver");
/*
                Class.forName(“полное имя класса”)
                Class.forName(“полное имя класса”).newInstance()
                DriverManager.registerDriver(new “полное имя класса”)
*/
        } catch (ClassNotFoundException e) {
            System.out.println("postgresql JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("postgresql JDBC Driver successfully connected");
        Connection connection = null;

        try {


            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);


            DatabaseMetaData meta = connection.getMetaData();


            System.out.println("\nReading data...");
            DatabaseLoader loader = new DatabaseLoader();
            loader.setCustomPropsFile(new File("C:\\Users\\Andrew\\wekafiles\\props\\DatabaseUtils.props.postgresql"));
            loader.setRetrieval(2);

            loader.setSource(DB_URL, USER, PASS);
            //--------------------------------------------------------------------
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


            //-------------------------------------------------------------------
            InstanceQuery query = new InstanceQuery();

            //-------------------------------------------------------------------


            //loader.connectToDatabase();
            //loader.setQuery("select * from mytable");
            loader.setQuery(result);
            // it might be necessary to define the columns that uniquely identify
            // a single row. Just provide them as comma-separated list:
            // loader.setKeys("col1,col2,...");
            Instances data2 = loader.getDataSet();
            Instances structure = loader.getStructure();
            Instances data = new Instances(structure);
            Instance inst;
            int count = 0;
            while ((inst = loader.getNextInstance(structure)) != null) {
                data.add(inst);
                count++;
                if ((count % 100) == 0)
                    System.out.println(count + " rows read so far.");
            }
            System.out.println(data.toSummaryString());
            //System.out.println(data);
            //set class index to the last attribute
            data.setClassIndex(data.numAttributes() - 1);

            J48 cls = new J48();
            PART part = new PART();
            // part.buildClassifier(data);
            //cls.buildClassifier(data);
            System.out.println(part);
            //  System.out.println(cls.graph());


            //load datasets
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("C:\\Program Files\\Weka-3-8\\data\\iris.arff");
            Instances dataset = source.getDataSet();
            //set class index to the last attribute
            dataset.setClassIndex(dataset.numAttributes() - 1);
            //create and build the classifier!
            J48 tree = new J48();
            tree.buildClassifier(dataset);
            System.out.println(tree);
            //  System.out.println(tree.toSource("classname"));


            //    part2.buildClassifier(dataset);





/*
            String[] options = new String[2];
            options[0] = "-R";
            options[1] = "1";
            Remove rm = new Remove();
            rm.setOptions(options);

            //use a simple filter to remove a certain attribute
            //set up options to remove 1st attribute
            String[] opts = new String[]{ "-R", "1"};
            //create a Remove object (this is the filter class)
            Remove remove = new Remove();
            //set the filter options
            remove.setOptions(opts);
            //pass the dataset to the filter
            remove.setInputFormat(dataset);
            //apply the filter
            Instances newData = Filter.useFilter(dataset, remove);

*/

            StringBuilder opt = new StringBuilder();
            //get number of attributes (notice class is not counted)
            int numAttr = data.numAttributes() - 1;
            System.out.println(data.checkForStringAttributes());
            for (int i = 0; i < numAttr; i++) {
                //check if current attr is of type nominal
                System.out.println(data.attribute(i).index());

                if (data.attribute(i).isString()) {

                    opt.append(" ").append(i);

                    System.out.println("The " + i + "th Attribute is String");
                    //get number of values
                    int n = data.attribute(i).numValues();
                    System.out.println("The " + i + "th Attribute has: " + n + " values");
                }

                //get an AttributeStats object
                AttributeStats as = data.attributeStats(i);
                int dC = as.distinctCount;
                System.out.println("The " + i + "th Attribute has: " + dC + " distinct values");

                //get a Stats object from the AttributeStats
                if (data.attribute(i).isNumeric()) {
                    System.out.println("The " + i + "th Attribute is Numeric");
                    Stats s = as.numericStats;
                    System.out.println("The " + i + "th Attribute has min value: " + s.min + " and max value: " + s.max + " and mean value: " + s.mean);
                }
            }


            String[] opts = new String[]{"-R", "1"};

            // options[1] = "1-2"; //set the attributes from indices 1 to 2 as
            String[] options = Utils.splitOptions("-R " + opt);
            System.out.println(data.toSummaryString());
            System.out.println(opt);
            StringToNominal stringToNominal = new StringToNominal();
            stringToNominal.setOptions(opts);
            System.out.println(stringToNominal.getAttributeRange());
            System.out.println(Arrays.toString(stringToNominal.getOptions()));
            stringToNominal.setInputFormat(data);
            Instances newData = Filter.useFilter(data, stringToNominal);
            //stringToNominal.setAttributeRange("-R"+opt);
            // stringToNominal.setInputFormat(data);

            String[] optsD = new String[]{"-R", "1,2,3,6,7,13,15"};
            //create a Remove object (this is the filter class)
            Remove remove = new Remove();
            //set the filter options
            remove.setOptions(optsD);
            //pass the dataset to the filter
            remove.setInputFormat(data);
            // remove.input(data);
            //apply the filter
            Instances newDataD = Filter.useFilter(data, remove);


            System.out.println(data.toSummaryString());
            System.out.println("**********************************************************");
            System.out.println(newDataD.checkForStringAttributes());
            System.out.println(newDataD.toSummaryString());
            System.out.println(newDataD.numAttributes());
            //pass the dataset to the filter
            //remove.setInputFormat(dataset);
            //apply the filter
            //Instances newData = Filter.useFilter(dataset, remove);


            PART part2 = new PART();
            part2.buildClassifier(newDataD);
            System.out.println(part2);
            // System.out.println(part2.getOptions());
            //  System.out.println(cls.graph());


            //  MakeDecList makeDecList= new MakeDecList(part);
            // output predictions
      /*      System.out.println("# - actual - predicted - error - distribution");
            for (int i = 0; i < data.numInstances(); i++) {
                double pred = cls.classifyInstance(data.instance(i));
                double[] dist = cls.distributionForInstance(data.instance(i));
                System.out.print((i+1));
                System.out.print(" - ");
                System.out.print(data.instance(i).toString(data.classIndex()));
                System.out.print(" - ");
                System.out.print(data.classAttribute().value((int) pred));
                System.out.print(" - ");
                if (pred != data.instance(i).classValue())
                    System.out.print("yes");
                else
                    System.out.print("no");
                System.out.print(" - ");
                System.out.print(Utils.arrayToString(dist));
                System.out.println();
            }
*/

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }

}

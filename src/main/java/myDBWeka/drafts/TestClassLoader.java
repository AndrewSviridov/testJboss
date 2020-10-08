package myDBWeka.drafts;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.AbstractLoader;
import weka.core.converters.DatabaseConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public class TestClassLoader extends AbstractLoader {
    private Instances m_structure;
    private Instances m_datasetPseudoInc;
    private boolean m_pseudoIncremental = true; // псевдо инкреминтал
    private Connection m_DataBaseConnection;
    private ResultSet resultSet;

    /**
     * The user defined query to load instances. (form: SELECT
     * *|&ltcolumn-list&gt; FROM &lttable&gt; [WHERE &lt;condition&gt;])
     */
    private String m_query;
    /**
     * The prepared statement used for database queries.
     */
    protected transient PreparedStatement m_PreparedStatement;

    public String getM_query() {
        return m_query;
    }

    public void setM_query(String m_query) {
        this.m_query = m_query;
    }

    /**
     * Stores the index of a nominal value
     */
    protected Hashtable<String, Double>[] m_nominalIndexes;

    /**
     * Stores the nominal value
     */
    protected ArrayList<String>[] m_nominalStrings;

    /**
     * the custom props file to use instead of default one.
     */
    //  protected File m_CustomPropsFile = new File("${user.home}");
    protected File m_CustomPropsFile = new File(PROPERTY_FILE);

    /**
     * Constructor
     *
     * @throws Exception if initialization fails
     */
    public TestClassLoader() throws Exception {

        /**
         * Initializes a new DatabaseConnection object, either default one or from
         * custom props file.
         *
         * @return the DatabaseConnection object
         * @see #m_CustomPropsFile
         */
        protected DatabaseConnection newDatabaseConnection () throws Exception {
            DatabaseConnection result;

            PROPERTIES = loadProperties(new File(PROPERTY_FILE));


            if (m_CustomPropsFile != null) {
                File pFile = new File(m_CustomPropsFile.getPath());
                String pPath = m_CustomPropsFile.getPath();
                try {
                    pPath = m_env.substitute(pPath);
                    pFile = new File(pPath);
                } catch (Exception ex) {
                }
                result = new DatabaseConnection(pFile);
            } else {
                result = new DatabaseConnection();
            }

            m_pseudoIncremental = false;
            m_checkForTable = true;
            String props = result.getProperties().getProperty("nominalToStringLimit");
            m_nominalToStringLimit = Integer.parseInt(props);
            m_idColumn = result.getProperties().getProperty("idColumn");
            if (result.getProperties().getProperty("checkForTable", "")
                    .equalsIgnoreCase("FALSE")) {
                m_checkForTable = false;
            }

            return result;
        }


    }

    public void setConnection(Connection connection) {
        this.m_DataBaseConnection = connection;
    }

    public ResultSet getResultSet() {

        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * Determines and returns (if possible) the structure (internally the header)
     * of the data set as an empty set of instances.
     *
     * @return the structure of the data set as an empty set of Instances
     * @throws IOException if an error occurs
     */
    @Override
    public Instances getStructure() throws IOException {

        if (m_DataBaseConnection == null) {
            throw new IOException("No source database has been specified");
        }


        pseudo:
        try {
            /*if (m_pseudoIncremental && m_structure == null) {
                if (getRetrieval() == BATCH) {
                    throw new IOException(
                            "Cannot mix getting instances in both incremental and batch modes");
                }
                setRetrieval(NONE);
                m_datasetPseudoInc = getDataSet();
                m_structure = new Instances(m_datasetPseudoInc, 0);
                setRetrieval(NONE);
                return m_structure;
            }
            */
            if (m_structure == null) {
            /*    if (m_checkForTable) {
                    if (!m_DataBaseConnection.tableExists(endOfQuery(true))) {
                        throw new IOException(
                                "Table does not exist according to metadata from JDBC driver. "
                                        + "If you are convinced the table exists, set 'checkForTable' "
                                        + "to 'False' in your DatabaseUtils.props file and try again.");
                    }
                }
            */
                // finds out which SQL statement to use for the DBMS to limit the number
                // of resulting rows to one
           /*     int choice = 0;
                boolean rightChoice = false;
                while (!rightChoice) {
                    try {
                        String limitQ = limitQuery(m_query, 0, choice);
                        if (m_DataBaseConnection.execute(limitQ) == false) {
                            throw new IOException("Query didn't produce results");
                        }
                        m_choice = choice;
                        rightChoice = true;
                    } catch (SQLException ex) {
                        choice++;
                        if (choice == 3) {
                            System.out
                                    .println("Incremental loading not supported for that DBMS. Pseudoincremental mode is used if you use incremental loading.\nAll rows are loaded into memory once and retrieved incrementally from memory instead of from the database.");
                            m_pseudoIncremental = true;
                            break pseudo;
                        }
                    }
                }

                String end = endOfQuery(false);
            */
                ResultSet rs = getResultSet();

                ResultSetMetaData md = rs.getMetaData();
                // rs.close();
                int numAttributes = md.getColumnCount();
                int[] attributeTypes = new int[numAttributes];
                m_nominalIndexes = Utils.cast(new Hashtable[numAttributes]);
                m_nominalStrings = Utils.cast(new ArrayList[numAttributes]);
                for (int i = 1; i <= numAttributes; i++) {
                    //     switch (m_DataBaseConnection.translateDBColumnType(md.getColumnTypeName(i))) {
                    switch (translateDBColumnType(md.getColumnTypeName(i))) {
                        case DatabaseConnection.STRING:

                            String columnName = md.getColumnLabel(i);
                            //   if (m_DataBaseConnection.getUpperCase()) {
                            if (getUpperCase()) {
                                columnName = columnName.toUpperCase();
                            }

                            m_nominalIndexes[i - 1] = new Hashtable<String, Double>();
                            m_nominalStrings[i - 1] = new ArrayList<String>();

                            // fast incomplete structure for batch mode - actual
                            // structure is determined by InstanceQuery in getDataSet()
                            if (getRetrieval() != INCREMENTAL) {
                                attributeTypes[i - 1] = Attribute.STRING;
                                break;
                            }
                            // System.err.println("String --> nominal");
                            ResultSet rs1;

                            //String query = "SELECT COUNT(DISTINCT( " + columnName + " )) FROM " + end;
                            String query = "SELECT COUNT(DISTINCT( " + columnName + " )) FROM " + m_query;
                            //if (m_DataBaseConnection.execute(query) == true) {
                            if (execute(query) == true) {
                                // rs1 = m_DataBaseConnection.getResultSet();
                                rs1 = getResultSet();
                                rs1.next();
                                int count = rs1.getInt(1);
                                rs1.close();
                                // if(count > m_nominalToStringLimit ||
                                // m_DataBaseConnection.execute("SELECT DISTINCT ( "+columnName+" ) FROM "+
                                // end) == false){
                                if (count > m_nominalToStringLimit
                                        || m_DataBaseConnection.execute("SELECT DISTINCT ( "
                                        + columnName + " ) FROM " + end + " ORDER BY " + columnName) == false) {
                                    attributeTypes[i - 1] = Attribute.STRING;
                                    break;
                                }
                                rs1 = m_DataBaseConnection.getResultSet();
                            } else {
                                // System.err.println("Count for nominal values cannot be calculated. Attribute "+columnName+" treated as String.");
                                attributeTypes[i - 1] = Attribute.STRING;
                                break;
                            }
                            attributeTypes[i - 1] = Attribute.NOMINAL;
                            stringToNominal(rs1, i);
                            rs1.close();
                            break;
                        case DatabaseConnection.TEXT:
                            // System.err.println("boolean --> string");

                            columnName = md.getColumnLabel(i);
                            if (m_DataBaseConnection.getUpperCase()) {
                                columnName = columnName.toUpperCase();
                            }

                            m_nominalIndexes[i - 1] = new Hashtable<String, Double>();
                            m_nominalStrings[i - 1] = new ArrayList<String>();

                            // fast incomplete structure for batch mode - actual
                            // structure is determined by InstanceQuery in getDataSet()
                            if (getRetrieval() != INCREMENTAL) {
                                attributeTypes[i - 1] = Attribute.STRING;
                                break;
                            }

                            query = "SELECT COUNT(DISTINCT( " + columnName + " )) FROM " + end;
                            if (m_DataBaseConnection.execute(query) == true) {
                                rs1 = m_DataBaseConnection.getResultSet();
                                stringToNominal(rs1, i);
                                rs1.close();
                            }
                            attributeTypes[i - 1] = Attribute.STRING;
                            break;
                        case DatabaseConnection.BOOL:
                            // System.err.println("boolean --> nominal");
                            attributeTypes[i - 1] = Attribute.NOMINAL;
                            m_nominalIndexes[i - 1] = new Hashtable<String, Double>();
                            m_nominalIndexes[i - 1].put("false", new Double(0));
                            m_nominalIndexes[i - 1].put("true", new Double(1));
                            m_nominalStrings[i - 1] = new ArrayList<String>();
                            m_nominalStrings[i - 1].add("false");
                            m_nominalStrings[i - 1].add("true");
                            break;
                        case DatabaseConnection.DOUBLE:
                            // System.err.println("BigDecimal --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.BYTE:
                            // System.err.println("byte --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.SHORT:
                            // System.err.println("short --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.INTEGER:
                            // System.err.println("int --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.LONG:
                            // System.err.println("long --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.FLOAT:
                            // System.err.println("float --> numeric");
                            attributeTypes[i - 1] = Attribute.NUMERIC;
                            break;
                        case DatabaseConnection.DATE:
                            attributeTypes[i - 1] = Attribute.DATE;
                            break;
                        case DatabaseConnection.TIME:
                            attributeTypes[i - 1] = Attribute.DATE;
                            break;
                        default:
                            // System.err.println("Unknown column type");
                            attributeTypes[i - 1] = Attribute.STRING;
                    }
                }
                ArrayList<Attribute> attribInfo = new ArrayList<Attribute>();
                for (int i = 0; i < numAttributes; i++) {
                    /* Fix for databases that uppercase column names */
                    // String attribName = attributeCaseFix(md.getColumnName(i + 1));
                    String attribName = md.getColumnLabel(i + 1);
                    switch (attributeTypes[i]) {
                        case Attribute.NOMINAL:
                            attribInfo.add(new Attribute(attribName, m_nominalStrings[i]));
                            break;
                        case Attribute.NUMERIC:
                            attribInfo.add(new Attribute(attribName));
                            break;
                        case Attribute.STRING:
                            Attribute att = new Attribute(attribName, (ArrayList<String>) null);
                            for (int n = 0; n < m_nominalStrings[i].size(); n++) {
                                att.addStringValue(m_nominalStrings[i].get(n));
                            }
                            attribInfo.add(att);
                            break;
                        case Attribute.DATE:
                            attribInfo.add(new Attribute(attribName, (String) null));
                            break;
                        default:
                            throw new IOException("Unknown attribute type");
                    }
                }
                m_structure = new Instances(endOfQuery(true), attribInfo, 0);
                // get rid of m_idColumn
                if (m_DataBaseConnection.getUpperCase()) {
                    m_idColumn = m_idColumn.toUpperCase();
                }
                // System.out.println(m_structure.attribute(0).name().equals(idColumn));
                if (m_structure.attribute(0).name().equals(m_idColumn)) {
                    m_oldStructure = new Instances(m_structure, 0);
                    m_oldStructure.deleteAttributeAt(0);
                    // System.out.println(m_structure);
                } else {
                    m_oldStructure = new Instances(m_structure, 0);
                }

                if (m_DataBaseConnection.getResultSet() != null) {
                    rs.close();
                }
            } else {
                if (m_oldStructure == null) {
                    m_oldStructure = new Instances(m_structure, 0);
                }
            }
            m_DataBaseConnection.disconnectFromDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
            printException(ex);
        }
        return m_oldStructure;

    }


    /**
     * Returns whether the cursors only support forward movement or are scroll
     * sensitive (with ResultSet.CONCUR_READ_ONLY concurrency). Returns always
     * false if not connected
     *
     * @return true if connected and the cursor is scroll-sensitive
     * @see ResultSet#TYPE_SCROLL_SENSITIVE
     * @see ResultSet#TYPE_FORWARD_ONLY
     * @see ResultSet#CONCUR_READ_ONLY
     */
    public boolean isCursorScrollSensitive() {
        boolean result;

        result = false;

        try {
            //  if (isConnected()) {
            result =
                    //m_Connection.getMetaData().supportsResultSetConcurrency(
                    m_DataBaseConnection.getMetaData().supportsResultSetConcurrency(
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //}
        } catch (Exception e) {
            // ignored
        }

        return result;
    }

    /**
     * Checks whether cursors are scrollable in general, false otherwise (also if
     * not connected).
     *
     * @return true if scrollable and connected
     * @see #getSupportedCursorScrollType()
     */
    public boolean isCursorScrollable() {
        return (getSupportedCursorScrollType() != -1);
    }

    /**
     * Returns the type of scrolling that the cursor supports, -1 if not supported
     * or not connected. Checks first for TYPE_SCROLL_SENSITIVE and then for
     * TYPE_SCROLL_INSENSITIVE. In both cases CONCUR_READ_ONLY as concurrency is
     * used.
     *
     * @return the scroll type, or -1 if not connected or no scrolling supported
     * @see ResultSet#TYPE_SCROLL_SENSITIVE
     * @see ResultSet#TYPE_SCROLL_INSENSITIVE
     */
    public int getSupportedCursorScrollType() {
        int result;

        result = -1;

        try {
            //  if (isConnected()) {

            //if (m_Connection.getMetaData().supportsResultSetConcurrency(
            if (m_DataBaseConnection.getMetaData().supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                result = ResultSet.TYPE_SCROLL_SENSITIVE;
            }

            if (result == -1) {
                //if (m_Connection.getMetaData().supportsResultSetConcurrency(
                if (m_DataBaseConnection.getMetaData().supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    result = ResultSet.TYPE_SCROLL_INSENSITIVE;
                }
            }
            //}
        } catch (Exception e) {
            // ignored
        }

        return result;
    }


    /**
     * Executes a SQL query. Caller must clean up manually with
     * <code>close()</code>.
     *
     * @param query the SQL query
     * @return true if the query generated results
     * @throws SQLException if an error occurs
     * @see// #close()
     */
    public boolean execute(String query) throws SQLException {
      /*  if (!isConnected()) {
            throw new IllegalStateException("Not connected, please connect first!");
        }
*/
        if (!isCursorScrollable()) {
            m_PreparedStatement =
                    // m_Connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
                    m_DataBaseConnection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
        } else {
            m_PreparedStatement =
                    // m_Connection.prepareStatement(query, getSupportedCursorScrollType(),
                    m_DataBaseConnection.prepareStatement(query, getSupportedCursorScrollType(),
                            ResultSet.CONCUR_READ_ONLY);
        }

        return (m_PreparedStatement.execute());
    }


    /**
     * Returns the table name or all after the FROM clause of the user specified
     * query to retrieve instances.
     *
     * @param onlyTableName true if only the table name should be returned, false
     *          otherwise
     * @return the end of the query
     */
  /*  private String endOfQuery(boolean onlyTableName) {
        String table;
        int beginIndex, endIndex;

        beginIndex = m_query.indexOf("FROM ") + 5;
        while (m_query.charAt(beginIndex) == ' ') {
            beginIndex++;
        }
        endIndex = m_query.indexOf(" ", beginIndex);
        if (endIndex != -1 && onlyTableName) {
            table = m_query.substring(beginIndex, endIndex);
        } else {
            table = m_query.substring(beginIndex);
        }
        if (m_DataBaseConnection.getUpperCase()) {
            table = table.toUpperCase();
        }
        return table;
    }

*/

    /**
     * For databases where Tables and Columns are created in upper case.
     */
    protected boolean m_checkForUpperCaseNames = false;

    /**
     * For databases where Tables and Columns are created in lower case.
     */
    protected boolean m_checkForLowerCaseNames = false;


    // from databaseConnection

    /**
     * Check if the property checkUpperCaseNames in the DatabaseUtils file is
     * set to true or false.
     *
     * @return true if the property checkUpperCaseNames in the DatabaseUtils
     * file is set to true, false otherwise.
     */
    public boolean getUpperCase() {
        return m_checkForUpperCaseNames;
    }


    /**
     * Properties associated with the database connection.
     */
    protected Properties PROPERTIES;

    public int translateDBColumnType(String type) {
        try {
            // Oracle, e.g., has datatypes like "DOUBLE PRECISION"
            // BUT property names can't have blanks in the name (unless escaped with
            // a backslash), hence also check for names where the blanks are
            // replaced with underscores "_":
            String value = PROPERTIES.getProperty(type);
            String typeUnderscore = type.replaceAll(" ", "_");
            if (value == null) {
                value = PROPERTIES.getProperty(typeUnderscore);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unknown data type: " + type + ". "
                    + "Add entry in " + PROPERTY_FILE + ".\n"
                    + "If the type contains blanks, either escape them with a backslash "
                    + "or use underscores instead of blanks.");
        }
    }

    /**
     * The name of the properties file.
     */
    public final static String PROPERTY_FILE = "C:\\Users\\Andrew\\wekafiles\\props\\DatabaseUtils.props.postgresql";
    //    "weka/experiment/DatabaseUtils.props";

    // databaseUtils
    private static Properties loadProperties(File propsFile) {
        Properties result;

        Properties defaultProps = null;
        try {
            defaultProps = Utils.readProperties(PROPERTY_FILE);
        } catch (Exception ex) {
            System.err.println("Warning, unable to read default properties file(s).");
            ex.printStackTrace();
        }

        if (propsFile == null) {
            return defaultProps;
        }
        if (!propsFile.exists() || propsFile.isDirectory()) {
            return defaultProps;
        }

        try {
            result = new Properties(defaultProps);
            result.load(new FileInputStream(propsFile));
        } catch (Exception e) {
            result = null;
            System.err
                    .println("Failed to load properties file (DatabaseUtils.java) '"
                            + propsFile + "':");
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Instances getDataSet() throws IOException {
        return null;
    }

    @Override
    public Instance getNextInstance(Instances structure) throws IOException {
        return null;
    }


    @Override
    public String getRevision() {
        return null;
    }
}

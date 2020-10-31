package DAO;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * @author KK JavaTutorials
 * Utility class which is responsible to get JDBC connection object using
 * Apache DBCP DataSource connection pool With MYSQL Database.
 */
public class C3POData {

    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_URL = "db.url";
    private static final String DB_DRIVER_CLASS = "driver.class.name";

    private static Properties properties = null;
    private static ComboPooledDataSource dataSource;

    static {
        try {
            properties = new Properties();//src/main/resources/config.properties
            properties.load(new FileInputStream("src/main/resources/database.properties"));
            //private val cpds =
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setJdbcUrl(properties.getProperty(DB_URL));
            dataSource.setUser(properties.getProperty(DB_USERNAME));
            dataSource.setPassword(properties.getProperty(DB_PASSWORD));

            // dataSource.setMinIdle(100);
            //dataSource.setMaxIdle(1000);
            dataSource.setInitialPoolSize(5);
            dataSource.setMinPoolSize(5);
            dataSource.setAcquireIncrement(5);
            dataSource.setMaxPoolSize(10);
            dataSource.setMaxStatements(100);


        } catch (IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

}







import java.sql.SQLException;


public class Service {


    public static void main(String[] args) throws SQLException {


        treeDatabase test = new treeDatabase(C3POData.getDataSource().getConnection());
        //test.getRule();
        //test.getData();
        // test.getFunctionData();
        test.myTest();
    }
}

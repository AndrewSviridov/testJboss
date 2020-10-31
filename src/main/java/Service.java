import DAO.C3POData;

import java.sql.SQLException;


public class Service {


    public static void main(String[] args) throws SQLException {


        treeDatabase2 test2 = new treeDatabase2(C3POData.getDataSource().getConnection());
        treeDatabase3 test3 = new treeDatabase3(C3POData.getDataSource().getConnection());
        //test.getRule();
        //test.getData();
        // test.getFunctionData();
        test2.myTest();

    }
}

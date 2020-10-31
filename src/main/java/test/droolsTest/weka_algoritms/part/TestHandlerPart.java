package test.droolsTest.weka_algoritms.part;

import DAO.C3POData;
import javafx.util.Pair;
import rule.KnowledgeBaseWeka;
import test.droolsTest.droolsUtils.DroolsHelper;
import test.droolsTest.weka_algoritms.JRIP.HandlerJRIP;
import weka.core.Attribute;
import weka.core.Instances;

import java.sql.Connection;
import java.util.HashMap;

public class TestHandlerPart {

    public static void main(String[] args) throws Exception {
        Connection conn = C3POData.getDataSource().getConnection();

        String[] arr = {"public.view03", "record_id", "namefields", "fieldvalue", "type_collum"};
        String path = "C:\\Users\\Andrew\\wekafiles\\props\\postgres\\DatabaseUtils.props";
        Pair<Instances, HashMap<String, Class>> pair = DroolsHelper.getInstancesFromDB(conn, arr, path);
        //HashMap<String, Class> stringClassMapAttributes= DroolsHelper.

        //       System.out.println(instances.toSummaryString());
        //System.out.println(data);
        // todo класс для классификации нужно передевать как нибудь
        Attribute attrClass = pair.getKey().attribute("_expassessment");

        //set class index to the last attribute

        System.out.println(pair.getKey().numAttributes() - 1);
        pair.getKey().setClassIndex(attrClass.index());

        test1(pair);
    }

    public static void test1(Pair<Instances, HashMap<String, Class>> data) throws Exception {
        HandlerJRIP handlerJRIP = new HandlerJRIP();
        KnowledgeBaseWeka knowledgeBaseWeka = handlerJRIP.getRules(data);
        System.out.println();
    }

}

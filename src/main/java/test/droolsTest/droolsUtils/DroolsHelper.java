package test.droolsTest.droolsUtils;

import javafx.util.Pair;
import myDBWeka.myDB_InstanceQuery;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import weka.core.Instances;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DroolsHelper {


    public static Pair<Instances, HashMap<String, Class>> getInstancesFromDB(Connection connection, String[] paramsReq, String pathProps) {

        Instances data = null;
        HashMap<String, Class> map = null;
        try {

            String result = "";
            CallableStatement callableStatement =
                    connection.prepareCall("{? = call pivotcode2(?,?,?,?,?)}");

            // output
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // input
            int i = 2;
            for (String it : paramsReq) {
                callableStatement.setString(i, it);
                i++;
            }
        /*
        callableStatement.setString(2, "public.view03");
        callableStatement.setString(3, "record_id");
        callableStatement.setString(4, "namefields");
        callableStatement.setString(5, "fieldvalue");
        callableStatement.setString(6, "type_collum");
        */

            // Run  function
            callableStatement.execute();

            // Get result
            result = callableStatement.getString(1);

            //System.out.println(result + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //InstanceQuery

            myDB_InstanceQuery query = new myDB_InstanceQuery();

            query.setM_Connection(connection);
            // System.out.println(query.customPropsFileTipText());
            query.setCustomPropsFile(new File(pathProps));
            query.setQuery(result);

            data = query.retrieveInstances();

            map = query.getStringClassMapAttributes();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pair<Instances, HashMap<String, Class>> pair = new Pair<>(data, map);
        return pair;
    }

    //data.classAttribute().name() == > namesDeclareClass
    public static String createDRLFileInMemory(Instances data, KnowledgeBaseWeka knowledgeBaseWeka, HashMap<String, Class> attributesClasses, String namesDeclareClass) {

        StringBuilder stringBuilder = new StringBuilder();
        //todo или из проперти или динамически
        stringBuilder
                .append("package org.kie1; \n\n ")
                .append("import test.droolsTest.ClassForGlobal; \n\n")
                .append("global java.util.List list; \n\n")
                .append("dialect  \"mvel\" \n\n")
                .append("\n" +
                        "\n" +
                        "declare " + namesDeclareClass)
                .append("\n\n");

//---------------------------------------------- создание класса
        Set<Map.Entry<String, Class>> entrySet = attributesClasses.entrySet();
        for (HashMap.Entry<String, Class> entry : entrySet) {
            stringBuilder.append(entry.getKey() + " : " + entry.getValue().getSimpleName());

            //do something with the key and value
        }

        stringBuilder.append("\nend\n\n");


        for (int i = 0; i < knowledgeBaseWeka.getRuleForWekaArrayList().size(); i++) {
            stringBuilder.append("rule Rule_" + i + " when\n");
            stringBuilder.append(data.classAttribute().name() + "(");
            for (int j = 0; j < knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().size(); j++) {
                ConditionForWeka conditionForWeka = knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().get(j);

                if (attributesClasses.containsKey(conditionForWeka.getField())) {
                    if (attributesClasses.get(conditionForWeka.getField()).getSimpleName() == "String") {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + "\"" + conditionForWeka.getValue() + "\"");
                        continue;
                    }
                }

                stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + conditionForWeka.getValue());

                stringBuilder.append(" && ");
            }
            stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
            stringBuilder.append(") \n");
            stringBuilder.append("then \n");
            stringBuilder.append("list.add(new ClassForGlobal(");
            stringBuilder.append("\"" + knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getThenPart() + "\",");
            stringBuilder.append("\"" + knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getInfo() + "\",");
            stringBuilder.append("\"Rule_" + i + "\"));");

            stringBuilder.append("\nend;\n\n");

        }


//    System.out.println(stringBuilder.toString() + "\n\n");
        return stringBuilder.toString() + "\n\n";


    }


}

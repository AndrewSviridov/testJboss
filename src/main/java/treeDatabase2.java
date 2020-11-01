
//STEP 1. Import required packages

import myDBWeka.myDB_InstanceQuery;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import test.droolsTest.ClassForGlobal;
import test.droolsTest.weka_algoritms.part.HandlerPart;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class treeDatabase2 {
    Connection connection;

    public treeDatabase2(Connection con) {
        connection = con;
    }


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

            System.out.println(result + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //InstanceQuery

            myDB_InstanceQuery query = new myDB_InstanceQuery();

            query.setM_Connection(connection);
            System.out.println(query.customPropsFileTipText());
            query.setCustomPropsFile(new File("C:\\Users\\Andrew\\wekafiles\\props\\postgres\\DatabaseUtils.props"));
            query.setQuery(result);

            Instances data = query.retrieveInstances();

            System.out.println("------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------");
            //  HashMap<String, Class> regrg = query.getStringClassMapAttributes();
            System.out.println(query.getStringClassMapAttributes().toString());



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

            Attribute attrClass = data.attribute("_expassessment");


            //set class index to the last attribute
            // data.setClassIndex(data.numAttributes() - 1);
            System.out.println(data.numAttributes() - 1);
            data.setClassIndex(attrClass.index());

            HandlerPart handlerPart = new HandlerPart();
            KnowledgeBaseWeka knowledgeBaseWeka = handlerPart.getRulesOld(data);


            //---------------------------------------------------- создание правил

        /*    "rule \"rule 1\" when \n" +
                    "    m : TestMessage( ) \n" +
                    "then \n" +
                    "    out.println( m.getName() + \": \" +  m.getText() ); \n" +
                    "end \n" +
                    "rule \"rule 2\" when \n" +
                    "    TestMessage( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                    "then \n" +
                    "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                    "end";
*/

            //   import test.droolsTest.createClass.TestMessage.test44;


            StringBuilder stringBuilder = new StringBuilder();
            //todo или из проперти или динамически
            stringBuilder
                    .append("package org.kie1; \n\n ")
                    .append("import test.droolsTest.ClassForGlobal; \n\n")
                    .append("global java.util.List list; \n\n")
                    .append("dialect  \"mvel\" \n\n")
                    .append("\n" +
                            "\n" +
                            "declare " + data.classAttribute().name())
                    .append("\n\n");

//---------------------------------------------- создание класса

            // int collums = data.numAttributes() - 1;

            for (int i = 0; i < data.numAttributes(); i++) {
                /* Fix for databases that uppercase column names */
                // String attribName = attributeCaseFix(md.getColumnName(i + 1));

                Attribute currentAttribute = data.attribute(i);

                if (data.attribute(i).isNominal()) {
                    stringBuilder.append(currentAttribute.name() + " : String\n");
                }
                if (data.attribute(i).isNumeric()) {
                    stringBuilder.append(currentAttribute.name() + " : Long\n");
                }
                if (data.attribute(i).isString()) {
                    stringBuilder.append(currentAttribute.name() + " : String\n");
                }
                if (data.attribute(i).isDate()) {
                    stringBuilder.append(currentAttribute.name() + " : String\n");
                }
                if (data.attribute(i).isRelationValued()) {
                    System.out.println("attribute is RelationValued");
                }
            }
            stringBuilder.append("\nend\n\n");


            for (int i = 0; i < knowledgeBaseWeka.getRuleForWekaArrayList().size(); i++) {
                //RuleForWeka listCondition=knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().get();
                stringBuilder.append("rule Rule_" + i + " when\n");
                stringBuilder.append(data.classAttribute().name() + "(");
                for (int j = 0; j < knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().size(); j++) {
                    ConditionForWeka conditionForWeka = knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().get(j);

                    if (getType(data.attribute(conditionForWeka.getField())).equals("java.lang.String")) {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + "\"" + conditionForWeka.getValue() + "\"");
                    }

                    if (getType(data.attribute(conditionForWeka.getField())).equals("java.lang.Long")) {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + conditionForWeka.getValue());
                    }
                    //System.out.println(ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()));
                    //System.out.println(classBuilderLoaded.getDeclaredField(conditionForWeka.getField()).getType().toString());
                    // System.out.println(classBuilderLoaded.getDeclaredField(conditionForWeka.getField()).getType().equals(Class.forName("java.lang.String")));
                 /*   if (classBuilderLoaded.getDeclaredField(conditionForWeka.getField()).getType().equals(Class.forName("java.lang.String"))) {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + "\""+conditionForWeka.getValue()+"\"");
                    }else {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + conditionForWeka.getValue());
                    }*/
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


            System.out.println(stringBuilder.toString() + "\n\n");

            String drlInMemeory = stringBuilder.toString();
// работа с drools


            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem();


            kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
            KieModuleModel km = ks.newKieModuleModel();
            km.newKieBaseModel("rules")
                    .addPackage("org.kie1");

            km.newKieBaseModel("rules").setDefault(true);

            kfs.writeKModuleXML(km.toXML());

            KieResources kr = ks.getResources();
            Resource r1 = kr.newByteArrayResource(stringBuilder.toString().getBytes())
                    .setResourceType(ResourceType.DRL)
                    .setSourcePath("org/kie1/myRules.drl");

            kfs.write(r1);

            KieBuilder builder = ks.newKieBuilder(kfs);

            builder.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
            if (builder.getResults().hasMessages(Message.Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n" + builder.getResults().toString());
            }

            //assertEquals( Collections.emptyList(), builder.getResults().getMessages( Message.Level.ERROR ) );

            System.out.println(builder.getKieModule().getReleaseId());

            KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
            FactType ft = kc.getKieBase("rules").getFactType("org.kie1", data.classAttribute().name());

            System.out.println(ft.getName());


         /*   assertNotNull( ft );
            assertNotNull( ft.getFactClass() );
            assertEquals( "org.drools.compiler.test1.Parent", ft.getFactClass().getSuperclass().getName() );
*/


  /*          Object first = ft.newInstance();
            ft.set(first, "myField", "Hello, HAL. Do you read me, HAL?!");

            kSession.insert(first);
            kSession.fireAllRules();
            Object outObj = kSession.getGlobal("out");
            System.out.println(outObj.toString());

            Object foo = null;
            try {
                foo = ft.newInstance();
                ft.set( foo, "myField", "bar" );
                assertEquals( "bar", type.get( foo, "myField" ) );
            } catch ( InstantiationException e ) {
                fail( e.getMessage() );
            } catch ( IllegalAccessException e ) {
                fail( e.getMessage() );
            }
*/

        /*    KieSession session = kieBase.newKieSession();
            FactHandle handle = session.insert( foo );
            int n = session.fireAllRules( 5 );
*/


            KieSession kSession = kc.getKieBase("rules").newKieSession();

            List<ClassForGlobal> list = new ArrayList<>();
            kSession.setGlobal("list", list);

            // вставить

            Object fcObject = null;
            try {
                fcObject = ft.newInstance();
                System.out.println(ft.getFields());
                ft.set(fcObject, "_bathfullness", 2L);
                //assertEquals( "bar", type.get( fcObject, "myField" ) );
            } catch (InstantiationException e) {
                //fail( e.getMessage() );
            } catch (IllegalAccessException e) {
                //fail( e.getMessage() );
            }


            // kSession.insert(new test.Message("Dave", "Hello, HAL. Do you read me, HAL?"));
            kSession.fireAllRules();
            List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");

            Long sumRules = 0L;
            for (ClassForGlobal item : outputGlobalList) {
                sumRules = sumRules + Long.parseLong(item.getThenPart());
                System.out.println(item.toString());
            }

            System.out.println();
            if (sumRules != 0)
                System.out.println("Средняя оценка " + sumRules / outputGlobalList.size());


            //     ksession.setGlobal("list", list);

/*
            StringBuilder opt = new StringBuilder();
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

                if (data.attribute(i).isNominal()) {
                    System.out.println("The " + i + "th Attribute is Numeric");
                    Stats s = as.numericStats;
                    System.out.println("The " + i + "th Attribute has min value: " + s.min + " and max value: " + s.max + " and mean value: " + s.mean);
                }


            }



            System.out.println(opt);


 */

            System.out.println();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getType(Attribute attribute) {
        //Class.forName("java.lang.String")
        String result = "";
        if (attribute.isNominal()) {
            result = "java.lang.String";
        }
        if (attribute.isNumeric()) {
            result = "java.lang.Long";
        }
        if (attribute.isString()) {
            result = "java.lang.String";
        }
        if (attribute.isDate()) {
            result = "java.lang.String";
        }
        if (attribute.isRelationValued()) {
            result = "attribute is RelationValued";
        }
        return result;
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



package test.droolsTest.weka_algoritms;

import javafx.util.Pair;
import rule.KnowledgeBaseWeka;
import weka.core.Instances;

import java.util.HashMap;

public interface IHandlerAlgorithm {
    KnowledgeBaseWeka getRules(Pair<Instances, HashMap<String, String>> pair) throws Exception;
}

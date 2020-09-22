package rule;

import java.util.ArrayList;

public class KnowledgeBaseWeka {
    private ArrayList<RuleForWeka> ruleForWekaArrayList;
    private String info = "";

    public KnowledgeBaseWeka() {
        this.ruleForWekaArrayList = new ArrayList<>();
    }

    public ArrayList<RuleForWeka> getRuleForWekaArrayList() {
        return ruleForWekaArrayList;
    }

    public void setRuleForWekaArrayList(ArrayList<RuleForWeka> ruleForWekaArrayList) {
        this.ruleForWekaArrayList = ruleForWekaArrayList;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

package rule;

import java.util.ArrayList;

public class RuleForWeka {
    private ArrayList<ConditionForWeka> list;
    private String thenPart = "";
    private String info = "";

    public RuleForWeka() {
        this.list = new ArrayList<>();

    }

    public ArrayList<ConditionForWeka> getList() {
        return list;
    }

    public void setList(ArrayList<ConditionForWeka> list) {
        this.list = list;
    }

    public String getThenPart() {
        return thenPart;
    }

    public void setThenPart(String thenPart) {
        this.thenPart = thenPart;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

package rule;

import java.util.ArrayList;
import java.util.Objects;

public class RuleForWeka {
    private String nameRule = null;
    private ArrayList<ConditionForWeka> list;
    private String thenPart = "";
    private String info = "";

    //todo добавить ruleName в констурктор может
    public RuleForWeka() {
        this.list = new ArrayList<>();

    }

    public String getNameRule() {
        return nameRule;
    }

    public void setNameRule(String nameRule) {
        this.nameRule = nameRule;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleForWeka that = (RuleForWeka) o;
        return Objects.equals(list, that.list) &&
                Objects.equals(thenPart, that.thenPart) &&
                Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list, thenPart, info);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

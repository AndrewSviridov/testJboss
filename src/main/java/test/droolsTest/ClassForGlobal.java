package test.droolsTest;

import java.util.Objects;

public class ClassForGlobal {
    String thenPart;
    String info;
    String nameRule;

    public ClassForGlobal(String thenPart, String info, String nameRule) {
        this.thenPart = thenPart;
        this.info = info;
        this.nameRule = nameRule;
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

    public String getNameRule() {
        return nameRule;
    }

    public void setNameRule(String nameRule) {
        this.nameRule = nameRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassForGlobal that = (ClassForGlobal) o;
        return Objects.equals(thenPart, that.thenPart) &&
                Objects.equals(info, that.info) &&
                Objects.equals(nameRule, that.nameRule);
    }

    @Override
    public String toString() {
        return "ClassForGlobal{" +
                "thenPart='" + thenPart + '\'' +
                ", info='" + info + '\'' +
                ", nameRule='" + nameRule + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(thenPart, info, nameRule);
    }
}

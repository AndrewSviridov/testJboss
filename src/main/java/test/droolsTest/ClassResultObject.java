package test.droolsTest;

import org.kie.api.definition.type.FactType;

import java.util.List;
import java.util.Objects;

public class ClassResultObject {
    private List<ClassForGlobal> outputGlobalList = null;
    private Object fcObject = null;
    private FactType factType = null;
    private Integer numberFire = 0;
    private Double assessment = 0.0;

    private double countAssessment(List<ClassForGlobal> outputGlobalList, Integer numberFire) {
        double summ = 0.0;
        if (numberFire != 0) {
            for (ClassForGlobal it : outputGlobalList) {
                summ = summ + Double.parseDouble(it.getThenPart());
            }
        } else return 0.0;
        return summ / numberFire;
    }

    public ClassResultObject() {
    }

    public ClassResultObject(List<ClassForGlobal> outputGlobalList, Object fcObject, Integer numberFire, FactType factType) {
        this.outputGlobalList = outputGlobalList;
        this.fcObject = fcObject;
        this.numberFire = numberFire;
        this.assessment = countAssessment(outputGlobalList, numberFire);
        this.factType = factType;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int n = 0; n < outputGlobalList.size(); n++) {
            stringBuilder.append(outputGlobalList.get(n)).append(" ");
            stringBuilder.append(n).append(" \n");

        }
        return "ClassResultObject{" +
                "outputGlobalList=" + stringBuilder.toString() +
                ", fcObject=" + fcObject +
                ", factType=" + factType +
                ", numberFire=" + numberFire +
                ", assessment=" + assessment +
                '}';
    }


    public String toStringCustom() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int n = 0; n < outputGlobalList.size(); n++) {
            stringBuilder.append(outputGlobalList.get(n)).append(" ");
            stringBuilder.append(n).append(" \n");

        }
        return "сколько сработало правил ЭС " + numberFire +
                "\n на какой факт они сработали\n" + fcObject.toString() +
                "\n какие данные дали правила\n" + stringBuilder.toString();

    }


    public List<ClassForGlobal> getOutputGlobalList() {
        return outputGlobalList;
    }

    public void setOutputGlobalList(List<ClassForGlobal> outputGlobalList) {
        this.outputGlobalList = outputGlobalList;
    }

    public Object getFcObject() {
        return fcObject;
    }

    public void setFcObject(Object fcObject) {
        this.fcObject = fcObject;
    }

    public Integer getNumberFire() {
        return numberFire;
    }

    public void setNumberFire(Integer numberFire) {
        this.numberFire = numberFire;
    }

    public Double getAssessment() {
        return assessment;
    }

    public FactType getFactType() {
        return factType;
    }

    public void setFactType(FactType factType) {
        this.factType = factType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassResultObject that = (ClassResultObject) o;
        return Objects.equals(outputGlobalList, that.outputGlobalList) &&
                Objects.equals(fcObject, that.fcObject) &&
                Objects.equals(factType, that.factType) &&
                numberFire.equals(that.numberFire) &&
                assessment.equals(that.assessment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputGlobalList, fcObject, factType, numberFire, assessment);
    }
}

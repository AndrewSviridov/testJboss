package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Variable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVariable;
    private String name;
    private long variableTypeIdVariableType;


    public long getIdVariable() {
        return idVariable;
    }

    public void setIdVariable(long idVariable) {
        this.idVariable = idVariable;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getVariableTypeIdVariableType() {
        return variableTypeIdVariableType;
    }

    public void setVariableTypeIdVariableType(long variableTypeIdVariableType) {
        this.variableTypeIdVariableType = variableTypeIdVariableType;
    }

}

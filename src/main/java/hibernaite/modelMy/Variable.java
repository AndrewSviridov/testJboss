package hibernaite.modelMy;


import javax.persistence.*;
import java.util.List;

public class Variable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVariable;
    private String name;
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "variableType_id", nullable = false)
    private List<VariableType> variableTypeIdVariableType;

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

    public List<hibernaite.modelMy.VariableType> getVariableTypeIdVariableType() {
        return variableTypeIdVariableType;
    }

    public void setVariableTypeIdVariableType(List<hibernaite.modelMy.VariableType> variableTypeIdVariableType) {
        this.variableTypeIdVariableType = variableTypeIdVariableType;
    }
}

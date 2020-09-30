package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class VariableType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVariableType;
    private String name;


    public long getIdVariableType() {
        return idVariableType;
    }

    public void setIdVariableType(long idVariableType) {
        this.idVariableType = idVariableType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

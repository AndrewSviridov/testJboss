package hibernaite.modelMy;


import javax.persistence.*;

public class VariableType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVariableType;
    private String name;
    //, cascade = CascadeType.ALL
    //@OneToMany(mappedBy = "VariableType", orphanRemoval = true)
    //
    @OneToMany(mappedBy = "Variable", cascade = CascadeType.ALL)
    private Variable varible;


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

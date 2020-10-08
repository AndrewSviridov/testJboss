package hibernaite.modelMy;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Signs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idSigns;
    private String name;
    private String signValue;


    public long getIdSigns() {
        return idSigns;
    }

    public void setIdSigns(long idSigns) {
        this.idSigns = idSigns;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSignValue() {
        return signValue;
    }

    public void setSignValue(String signValue) {
        this.signValue = signValue;
    }

}

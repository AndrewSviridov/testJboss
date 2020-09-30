package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Precedent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPrecedent;
    private String name;
    private long fieldsIkIdFields;
    private long elementIdelement;
    private long thenpart;


    public long getIdPrecedent() {
        return idPrecedent;
    }

    public void setIdPrecedent(long idPrecedent) {
        this.idPrecedent = idPrecedent;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getFieldsIkIdFields() {
        return fieldsIkIdFields;
    }

    public void setFieldsIkIdFields(long fieldsIkIdFields) {
        this.fieldsIkIdFields = fieldsIkIdFields;
    }


    public long getElementIdelement() {
        return elementIdelement;
    }

    public void setElementIdelement(long elementIdelement) {
        this.elementIdelement = elementIdelement;
    }


    public long getThenpart() {
        return thenpart;
    }

    public void setThenpart(long thenpart) {
        this.thenpart = thenpart;
    }

}

package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class FieldsIk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFields;
    private String nameFields;
    private String titlesTablesFields;
    private String uuidFields;
    private long idprecedent;


    public long getIdFields() {
        return idFields;
    }

    public void setIdFields(long idFields) {
        this.idFields = idFields;
    }


    public String getNameFields() {
        return nameFields;
    }

    public void setNameFields(String nameFields) {
        this.nameFields = nameFields;
    }


    public String getTitlesTablesFields() {
        return titlesTablesFields;
    }

    public void setTitlesTablesFields(String titlesTablesFields) {
        this.titlesTablesFields = titlesTablesFields;
    }


    public String getUuidFields() {
        return uuidFields;
    }

    public void setUuidFields(String uuidFields) {
        this.uuidFields = uuidFields;
    }


    public long getIdprecedent() {
        return idprecedent;
    }

    public void setIdprecedent(long idprecedent) {
        this.idprecedent = idprecedent;
    }

}

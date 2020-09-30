package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ValueS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idValueS;
    private long fieldsIkIdFields;
    private String fieldvalue;


    public long getIdValueS() {
        return idValueS;
    }

    public void setIdValueS(long idValueS) {
        this.idValueS = idValueS;
    }


    public long getFieldsIkIdFields() {
        return fieldsIkIdFields;
    }

    public void setFieldsIkIdFields(long fieldsIkIdFields) {
        this.fieldsIkIdFields = fieldsIkIdFields;
    }


    public String getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldvalue(String fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

}

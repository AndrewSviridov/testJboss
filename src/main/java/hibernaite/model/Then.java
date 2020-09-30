package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Then {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idthen;
    private String title;
    private String sovet;
    private long mark;


    public long getIdthen() {
        return idthen;
    }

    public void setIdthen(long idthen) {
        this.idthen = idthen;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSovet() {
        return sovet;
    }

    public void setSovet(String sovet) {
        this.sovet = sovet;
    }


    public long getMark() {
        return mark;
    }

    public void setMark(long mark) {
        this.mark = mark;
    }

}

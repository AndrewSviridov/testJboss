package hibernaite.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Rules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idrules;
    private long ssakValue;
    private long sslPerem;
    private long viragenie;
    private String path;
    private long sign;
    private long uuid;
    private long thenpart;
    private long elementIdelement;


    public long getIdrules() {
        return idrules;
    }

    public void setIdrules(long idrules) {
        this.idrules = idrules;
    }


    public long getSsakValue() {
        return ssakValue;
    }

    public void setSsakValue(long ssakValue) {
        this.ssakValue = ssakValue;
    }


    public long getSslPerem() {
        return sslPerem;
    }

    public void setSslPerem(long sslPerem) {
        this.sslPerem = sslPerem;
    }


    public long getViragenie() {
        return viragenie;
    }

    public void setViragenie(long viragenie) {
        this.viragenie = viragenie;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public long getSign() {
        return sign;
    }

    public void setSign(long sign) {
        this.sign = sign;
    }


    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }


    public long getThenpart() {
        return thenpart;
    }

    public void setThenpart(long thenpart) {
        this.thenpart = thenpart;
    }


    public long getElementIdelement() {
        return elementIdelement;
    }

    public void setElementIdelement(long elementIdelement) {
        this.elementIdelement = elementIdelement;
    }

}

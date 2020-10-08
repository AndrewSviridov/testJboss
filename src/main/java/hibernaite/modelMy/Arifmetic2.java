package hibernaite.modelMy;

import org.hibernate.annotations.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Arifmetic2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long ssakValue;
    private long sslPerem;
    private String path;
    private long sign;
    private String uuid;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}

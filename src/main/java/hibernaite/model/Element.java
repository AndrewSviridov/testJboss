package hibernaite.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idElement;
    private String name;
    private String description;
    private long userIdUser;


    public long getIdElement() {
        return idElement;
    }

    public void setIdElement(long idElement) {
        this.idElement = idElement;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public long getUserIdUser() {
        return userIdUser;
    }

    public void setUserIdUser(long userIdUser) {
        this.userIdUser = userIdUser;
    }

}

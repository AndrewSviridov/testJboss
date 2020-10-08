package hibernaite.model;

import java.util.Set;
import javax.persistence.*;


@Entity
@Table(name = "CARTOIO")
public class CartOIO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    @JoinColumn(name = "cart_id") // we need to duplicate the physical information
    private Set<ItemsOIO> items;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<ItemsOIO> getItems() {
        return items;
    }

    public void setItems(Set<ItemsOIO> items) {
        this.items = items;
    }

}

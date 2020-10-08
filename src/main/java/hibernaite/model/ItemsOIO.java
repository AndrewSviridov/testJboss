package hibernaite.model;

import javax.persistence.*;

@Entity
@Table(name = "ITEMSOIO")
public class ItemsOIO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private CartOIO cart;

    // Hibernate requires no-args constructor
    public ItemsOIO() {
    }

    public ItemsOIO(CartOIO c) {
        this.cart = c;
    }

    public CartOIO getCartOIO() {
        return cart;
    }

    public void setCartOIO(CartOIO cart) {
        this.cart = cart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}

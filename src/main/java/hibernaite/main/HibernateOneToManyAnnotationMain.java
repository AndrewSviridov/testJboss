package hibernaite.main;

import java.util.HashSet;
import java.util.Set;

import hibernaite.HibernateAnnotationUtil;
import hibernaite.model.Cart;
import hibernaite.model.Items;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class HibernateOneToManyAnnotationMain {

    public static void main(String[] args) {

        Cart cart = new Cart();

        Items item1 = new Items(cart);
        Items item2 = new Items(cart);
        Set<Items> itemsSet = new HashSet<Items>();
        itemsSet.add(item1);
        itemsSet.add(item2);

        cart.setItems(itemsSet);


        SessionFactory sessionFactory = null;
        Session session = null;
        Transaction tx = null;
        try {
            // Get Session
            sessionFactory = HibernateAnnotationUtil.getSessionFactory();
            session = sessionFactory.getCurrentSession();
            System.out.println("Session created");
            // start transaction
            tx = session.beginTransaction();
            // Save the Model object
            session.save(cart);
            session.save(item1);
            session.save(item2);
            // Commit transaction
            tx.commit();
            System.out.println("Cart ID=" + cart.getId());
            System.out.println("item1 ID=" + item1.getId() + ", Foreign Key Cart ID=" + item1.getCart().getId());
            System.out.println("item2 ID=" + item2.getId() + ", Foreign Key Cart ID=" + item1.getCart().getId());

        } catch (Exception e) {
            System.out.println("Exception occured. " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (!sessionFactory.isClosed()) {
                System.out.println("Closing SessionFactory");
                sessionFactory.close();
            }
        }
    }

}

package hibernaite.main;

import java.util.HashSet;
import java.util.Set;

import hibernaite.HibernateAnnotationUtil;
import hibernaite.model.CartOIO;
import hibernaite.model.ItemsOIO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class HibernateOneisOwningSide {
    public static void main(String[] args) {

        CartOIO cart = new CartOIO();
        CartOIO cart2 = new CartOIO();

        ItemsOIO item1 = new ItemsOIO(cart);
        ItemsOIO item2 = new ItemsOIO(cart2);
        Set<ItemsOIO> itemsSet = new HashSet<ItemsOIO>();
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
            session.save(cart2);
            session.save(item1);
            session.save(item2);
            // Commit transaction
            tx.commit();

            session = sessionFactory.getCurrentSession();
            tx = session.beginTransaction();
            item1 = session.get(ItemsOIO.class, new Long(1));
            item2 = session.get(ItemsOIO.class, new Long(2));
            tx.commit();

            System.out.println("item1 ID=" + item1.getId() + ", Foreign Key CartOIO ID=" + item1.getCartOIO()
                    .getId());
            System.out.println("item2 ID=" + item2.getId() + ", Foreign Key CartOIO ID=" + item2.getCartOIO()
                    .getId());

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

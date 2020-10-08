package hibernaite;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateAnnotationUtil {

    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            // Create the SessionFactory from hibernate-annotation.cfg.xml
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
            SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

            return sessionFactory;

        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
}

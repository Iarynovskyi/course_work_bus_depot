package org.buspark.util;

import org.buspark.model.Bus;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                Properties settings = new Properties();

                settings.put(Environment.DRIVER, "org.postgresql.Driver");

                settings.put(Environment.URL, System.getenv("DB_URL"));
                settings.put(Environment.USER, System.getenv("DB_USER"));
                settings.put(Environment.PASS, System.getenv("DB_PASS"));

                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.HBM2DDL_AUTO, "update");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Bus.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR: Hibernate initialization failed!");
                e.printStackTrace();
                throw new RuntimeException("Failed to create session factory");
            }
        }
        return sessionFactory;
    }
}
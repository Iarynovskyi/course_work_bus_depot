package org.buspark.util;

import org.buspark.model.Bus;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();
                settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
                settings.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/cursWork");
                settings.put("hibernate.connection.username", "postgres");
                settings.put("hibernate.connection.password", "твій_пароль");
                settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.hbm2ddl.auto", "update");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Bus.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
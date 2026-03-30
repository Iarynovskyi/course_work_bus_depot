package org.buspark.repository;

import org.buspark.model.Bus;
import org.buspark.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class BusRepository {
    public void saveOrUpdate(Bus bus) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(bus);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Bus> findByStatus(int status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Bus where status = :status order by busNumber", Bus.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    public Bus findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Bus.class, id);
        }
    }

    public List<Bus> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Bus", Bus.class).list();
        }
    }
}
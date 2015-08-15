package com.github.windbender;

import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.github.windbender.domain.Identification;

public class DAOTests {
    SessionFactory sessionFactory;

    public DAOTests() {
        Configuration config=new Configuration();
        config.setProperty("hibernate.connection.url","jdbc:mysql://localhost/wlcdm");
        config.setProperty("hibernate.connection.username","wlcdm");
        config.setProperty("hibernate.connection.password","wlcdm");
        config.setProperty("hibernate.connection.driver_class","com.mysql.jdbc.Driver");

        config.addAnnotatedClass(Identification.class); 

        sessionFactory=config.configure().buildSessionFactory();
    }

    public Session getSession()
    {
        Session session;

        try {
            session = sessionFactory.getCurrentSession();
        } catch (SessionException se) {
            session = sessionFactory.openSession();
        }

        return session;
    }
}

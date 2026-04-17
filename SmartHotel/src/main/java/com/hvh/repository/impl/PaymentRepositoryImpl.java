/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Payment;
import com.hvh.repository.PaymentRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 03358
 */
@Transactional
@Repository
public class PaymentRepositoryImpl implements PaymentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void addPayment(Payment p) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(p);
    }

    @Override
    public List<Payment> getPaymentsByReservation(long resId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Payment> q = b.createQuery(Payment.class);
        Root root = q.from(Payment.class);
        q.where(b.equal(root.get("reservation").get("id"), resId));
        return s.createQuery(q).getResultList();
    }
    
    
}

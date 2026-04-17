/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Invoice;
import com.hvh.repository.InvoiceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class InvoiceRepositoryImpl implements InvoiceRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Invoice> getInvoices(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Invoice> q = b.createQuery(Invoice.class);
        Root root = q.from(Invoice.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String userId = params.get("customerId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("customerProfile").get("id"), Long.parseLong(userId)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        return s.createQuery(q).getResultList();
    }

    @Override
    public void addOrUpdate(Invoice i) {
        Session s = this.factory.getObject().getCurrentSession();
        if (i.getId() != null) s.merge(i);
        else s.persist(i);
    }

    @Override
    public Invoice getById(Long id) {
        return this.factory.getObject().getCurrentSession().get(Invoice.class, id);
    }

    @Override
    public Invoice getByReservationId(Long resId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Invoice> q = b.createQuery(Invoice.class);
        Root root = q.from(Invoice.class);
        
        q.where(b.equal(root.get("reservation").get("id"), resId));
        
        return s.createQuery(q).uniqueResult();
    }
}

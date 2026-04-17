/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Reservation;
import com.hvh.repository.ReservationRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 03358
 */
@Repository
@Transactional
public class ReservationRepositoryImpl implements ReservationRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Autowired
    private Environment env;
    
    @Override
    public List<Reservation> getReservations(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Reservation> q = b.createQuery(Reservation.class);
        Root root = q.from(Reservation.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String customerId = params.get("customerId");
            if (customerId != null && !customerId.isEmpty()) {
                
                predicates.add(b.equal(root.get("customerProfile").get("id"), Long.parseLong(customerId)));
            }

            String status = params.get("status");
            if (status != null && !status.isEmpty()) {
                predicates.add(b.equal(root.get("reservationStatus"), status));
            }

            String fromDate = params.get("fromDate");
            if (fromDate != null && !fromDate.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("checkInDate"), fromDate));
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.desc(root.get("id")));

        Query query = session.createQuery(q);

        if (params != null && params.containsKey("page")) {
            int pageSize = this.env.getProperty("reservations.page_size", Integer.class, 10);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }

        return query.getResultList();
    }

    @Override
    public void addOrUpdateReservation(Reservation res) {
        Session session = this.factory.getObject().getCurrentSession();
        if (res.getId() != null) {
            session.merge(res);
        } else {
            session.persist(res);
        }
    }
    

    @Override
    public Reservation getReservationById(long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Reservation.class, id);
    }
}

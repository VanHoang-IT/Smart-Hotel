/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.ServiceOrder;
import com.hvh.repository.ServiceOrderRepository;
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
public class ServiceOrderRepositoryImpl implements ServiceOrderRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ServiceOrder> getServiceOrders(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ServiceOrder> q = b.createQuery(ServiceOrder.class);
        Root root = q.from(ServiceOrder.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            
            // Lọc theo Reservation ID (Để xem các dịch vụ của 1 đơn đặt phòng)
            String resId = params.get("reservationId");
            if (resId != null && !resId.isEmpty()) {
                predicates.add(b.equal(root.get("reservationId").get("id"), Long.parseLong(resId)));
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getResultList();
    }

    @Override
    public void addOrUpdate(ServiceOrder order) {
        Session s = this.factory.getObject().getCurrentSession();
        if (order.getId() != null) {
            s.merge(order);
        } else {
            s.persist(order);
        }
    }

    @Override
    public ServiceOrder getById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(ServiceOrder.class, id);
    }
}

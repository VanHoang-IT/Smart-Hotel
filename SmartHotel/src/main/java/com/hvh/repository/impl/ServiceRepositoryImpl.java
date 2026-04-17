/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Service;
import com.hvh.repository.ServiceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ServiceRepositoryImpl implements ServiceRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Service> getServices(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Service> q = b.createQuery(Service.class);
        Root root = q.from(Service.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("name"), String.format("%%%s%%", kw)));
            }

            String active = params.get("active");
            if (active != null && !active.isEmpty()) {
                predicates.add(b.equal(root.get("active"), Integer.parseInt(active)));
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getResultList();
    }

    @Override
    public void addOrUpdate(Service s) {
        Session session = this.factory.getObject().getCurrentSession();
        if(s.getId() != null){
            session.merge(s);
        }
        else{
            session.persist(s);
        }
    }

    @Override
    public Service getServiceById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Service.class, id);
    }

    @Override
    public void deleteService(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        Service s = this.getServiceById(id);
        session.remove(s);
    }
    
}

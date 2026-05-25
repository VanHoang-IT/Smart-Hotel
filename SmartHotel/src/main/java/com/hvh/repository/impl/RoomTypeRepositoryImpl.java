/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.RoomType;
import com.hvh.repository.RoomTypeRepository;
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
@Repository
@Transactional
public class RoomTypeRepositoryImpl implements RoomTypeRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<RoomType> getType() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<RoomType> q = b.createQuery(RoomType.class);
        Root<RoomType> root = q.from(RoomType.class);
        q.select(root);
        return session.createQuery(q).getResultList();
    }

    @Override
    public void addOrUpdate(RoomType rt) {
        Session session = this.factory.getObject().getCurrentSession();
        if (rt.getId() != null) {
            session.merge(rt);
        } else {
            session.persist(rt);
        }
    }

    @Override
    public RoomType getById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(RoomType.class, id);
    }

    @Override
    public void delete(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        RoomType rt = this.getById(id);
        if (rt != null) session.remove(rt);
    }
}

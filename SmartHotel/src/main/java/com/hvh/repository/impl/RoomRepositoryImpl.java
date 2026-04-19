/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Room;
import com.hvh.repository.RoomRepository;
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
import org.hibernate.query.Query;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author 03358
 */
@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class RoomRepositoryImpl implements RoomRepository{
    @Autowired
    private Environment env;
            
    private static final int PAGE_SIZE = 6;
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    
    @Override
    public List<Room> getRoom(Map<String, String> params){
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b =  session.getCriteriaBuilder();
        CriteriaQuery<Room> q = b.createQuery(Room.class);
        Root root = q.from(Room.class);
        q.select(root);
        
        if(params != null){
            List<Predicate> predicates = new ArrayList<>();
            
            String kw = params.get("kw");
            if(kw != null && !kw.isEmpty()){
                predicates.add(b.like(root.get("room_number"), String.format("%%%s%%", kw)));
            }
            
            String fromPrice = params.get("fromPrice");
            if (fromPrice != null && !fromPrice.isEmpty()){
                predicates.add(b.greaterThanOrEqualTo(root.get("price"), fromPrice));
            }
            
            String toPrice = params.get("toPrice");
            if (toPrice != null && !toPrice.isEmpty()){
                predicates.add(b.lessThanOrEqualTo(root.get("price"), toPrice));
            }
           
            q.where(predicates.toArray(Predicate[]::new));
        }
        
        q.orderBy(b.desc(root.get("id")));
        
        Query query = session.createQuery(q);
        
        if(params != null){
            int pageSize = this.env.getProperty("rooms.page_size", Integer.class);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * pageSize;
            
            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }
        return query.getResultList();
    }

    @Override
    public void addOrUpdateRoom(Room r) {
        Session session = this.factory.getObject().getCurrentSession();
        if(r.getId() != null){
            session.merge(r);
        }
        else {
            session.persist(r);
        }
    }

    @Override
    public Room getRoomById(long id) {
        Session session = this.factory.getObject().getCurrentSession();
            return session.get(Room.class, id);
    }

    @Override
    public void deleteRoom(long id) {
        Session session = this.factory.getObject().getCurrentSession();
        Room r = this.getRoomById(id);
        session.remove(r);
    }
}

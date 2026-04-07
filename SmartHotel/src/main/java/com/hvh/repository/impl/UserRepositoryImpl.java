/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Users;
import com.hvh.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
public class UserRepositoryImpl  implements UserRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public Users getUserByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        Query q = session.createNamedQuery("Users.findByUsername", Users.class);
        q.setParameter("username", username);
        
        return (Users)q.getSingleResult();
    }

    @Override
    public Users addUser(Users u) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(u);
        
        return u;
    }
    
}

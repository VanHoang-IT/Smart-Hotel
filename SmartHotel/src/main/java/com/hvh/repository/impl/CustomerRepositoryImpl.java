/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.CustomerProfile;
import com.hvh.repository.CustomerRepository;
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
public class CustomerRepositoryImpl implements CustomerRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public CustomerProfile getCustomerById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(CustomerProfile.class, id);
    }
    
    @Override
    public void addCustomerProfile(CustomerProfile profile) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(profile);
    }    
}

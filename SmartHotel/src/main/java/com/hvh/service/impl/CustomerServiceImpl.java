/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.User;
import com.hvh.repository.CustomerRepository;
import com.hvh.repository.UserRepository;
import com.hvh.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 03358
 */
@Service
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    private CustomerRepository cusRepo;
    
    @Autowired
    private UserRepository userRepo;

    @Override
    public CustomerProfile getCustomerById(Long id) {
        return this.cusRepo.getCustomerById(id);
    }

    @Override
    public CustomerProfile getCustomerByUserId(Long userId) {
        return this.cusRepo.getCustomerByUserId(userId);
    }

    @Override
    @Transactional
    public CustomerProfile createCustomerProfile(Long userId, CustomerProfile profileData) {
        User user = this.userRepo.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        CustomerProfile existing = this.cusRepo.getCustomerByUserId(userId);
        if (existing != null) {
            throw new IllegalStateException("CUSTOMER_PROFILE_ALREADY_EXISTS");
        }

        CustomerProfile profile = new CustomerProfile();
        profile.setUserId(user);
        profile.setDob(profileData.getDob());
        profile.setAddress(profileData.getAddress());
        profile.setLoyaltyPoint(0);
        this.cusRepo.addCustomerProfile(profile);
        return profile;
    }
}

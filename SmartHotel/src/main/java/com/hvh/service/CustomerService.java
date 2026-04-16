/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.CustomerProfile;

/**
 *
 * @author 03358
 */
public interface CustomerService {
    CustomerProfile getCustomerById(Long id);
}

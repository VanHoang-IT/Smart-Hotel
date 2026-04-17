/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.Service;
import com.hvh.repository.ServiceRepository;
import com.hvh.repository.ServiceService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 03358
 */
@Repository
public class ServiceServiceImpl implements ServiceService{
    @Autowired
    private ServiceRepository serviceRepo;
    
    
    @Override
    public List<Service> getServices(Map<String, String> params) {
        return this.serviceRepo.getServices(params);
    }

    @Override
    public void addOrUpdate(Service s) {
        this.serviceRepo.addOrUpdate(s);
    }

    @Override
    public Service getServiceById(Long id) {
        return this.serviceRepo.getServiceById(id);
    }

    @Override
    public void deleteService(Long id) {
        this.serviceRepo.deleteService(id);
    }
    
}

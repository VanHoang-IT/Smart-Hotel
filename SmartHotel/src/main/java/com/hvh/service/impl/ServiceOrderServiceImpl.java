/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.ServiceOrder;
import com.hvh.repository.ServiceOrderRepository;
import com.hvh.service.ServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ServiceOrderServiceImpl implements ServiceOrderService {

    @Autowired
    private ServiceOrderRepository serviceOrderRepo;

    @Override
    public List<ServiceOrder> getServiceOrders(Map<String, String> params) {
        return this.serviceOrderRepo.getServiceOrders(params);
    }

    @Override
    public void addOrUpdate(ServiceOrder order) {
        this.serviceOrderRepo.addOrUpdate(order);
    }

    @Override
    public ServiceOrder getById(Long id) {
        return this.serviceOrderRepo.getById(id);
    }
}

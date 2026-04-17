/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.ServiceOrder;
import java.util.List;
import java.util.Map;

public interface ServiceOrderService {
    List<ServiceOrder> getServiceOrders(Map<String, String> params);
    void addOrUpdate(ServiceOrder order);
    ServiceOrder getById(Long id);
}

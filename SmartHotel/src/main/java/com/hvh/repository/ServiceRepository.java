/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.Service;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */

public interface ServiceRepository {
    List<Service> getServices(Map<String, String> params);
    void addOrUpdate(Service s);
    Service getServiceById(Long id);
    void deleteService(Long id);
}

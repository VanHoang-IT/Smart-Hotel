/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Services;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */

public interface ServiceService {
    List<Services> getServices(Map<String, String> params);
    void addOrUpdate(Services s);
    Services getServiceById(Long id);
    void deleteService(Long id);
}

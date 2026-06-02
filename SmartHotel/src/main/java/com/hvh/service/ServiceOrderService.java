/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.dto.ServiceOrderRequestDTO;
import com.hvh.dto.ServiceOrderResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ServiceOrderService {
    List<ServiceOrderResponseDTO> getServiceOrders(Map<String, String> params);
    void addOrUpdate(ServiceOrderRequestDTO orderDto);
    ServiceOrderResponseDTO getById(Long id);
    void updateStatus(Long id, String status);
    BigDecimal getTotalAmountByReservation(Long resId);
}

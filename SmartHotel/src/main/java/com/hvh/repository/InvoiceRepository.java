/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.Invoice;
import java.util.List;
import java.util.Map;

public interface InvoiceRepository {
    List<Invoice> getInvoices(Map<String, String> params);
    void addOrUpdate(Invoice i);
    Invoice getById(Long id);
    Invoice getByReservationId(Long resId);
}

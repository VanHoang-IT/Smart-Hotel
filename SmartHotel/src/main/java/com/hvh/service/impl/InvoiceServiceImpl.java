/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.Invoice;
import com.hvh.repository.InvoiceRepository;
import com.hvh.service.InvoiceService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class InvoiceServiceImpl implements InvoiceService{
    
    @Autowired
    InvoiceRepository invoiceRepo;
    
    @Override
    public List<Invoice> getInvoices(Map<String, String> params) {
        return this.invoiceRepo.getInvoices(params);
    }

    @Override
    public void addOrUpdate(Invoice i) {
        this.invoiceRepo.addOrUpdate(i);
    }
    @Override
    public Invoice getById(Long id) {
        return this.invoiceRepo.getById(id);
    }

    @Override
    public Invoice getByReservationId(Long resId) {
        return this.getByReservationId(resId);
    }
    
}

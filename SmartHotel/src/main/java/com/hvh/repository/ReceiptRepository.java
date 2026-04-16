/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;


import com.hvh.dto.CartItem;
import java.util.Date;
import java.util.List;

/**
 *
 * @author 03358
 */
public interface ReceiptRepository {
    void addReceipt(List<CartItem> carts, Date checkIn, Date checkOut, Long customerId);
}

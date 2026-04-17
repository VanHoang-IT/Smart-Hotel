/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.CartItem;
import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.repository.CustomerRepository;
import com.hvh.repository.RoomRepository;
import com.hvh.repository.UserRepository;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hvh.repository.ReceiptRepository;

/**
 *
 * @author 03358
 */
@Repository
@Transactional
public class ReceiptRepositoryImpl implements ReceiptRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private RoomRepository roomRepo;
    
    private CustomerRepository cusRepo;
    
    @Override
    public void addReceipt(List<CartItem> carts, Date checkIn, Date checkOut, Long customerId) {
       Session session = this.factory.getObject().getCurrentSession();
       Reservation r = new Reservation();
       r.setCreatedAt(new Date());
       
       r.setCheckIn(checkIn);
       r.setCheckOut(checkOut);
       r.setStatus("PENDING");
       
       String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
       r.setCreatedBy(this.userRepo.getUserByUsername(currentUser));
       
       CustomerProfile customer = this.cusRepo.getCustomerById(customerId);
       r.setCustomerId(customer);
       
       session.persist(r);
       
       for(CartItem c : carts){
           ReservationRoom rr = new ReservationRoom();
           
           rr.setPricePerNight(c.getPrice());
           rr.setRoomId(this.roomRepo.getRoomById(c.getId()));
           rr.setReservationId(r);
           
           session.persist(rr);
       }
    }
}

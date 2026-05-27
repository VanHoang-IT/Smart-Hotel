/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.dto;
/**
 *
 * @author ASUS
 */
import java.util.Date;

public class CustomerProfileDTO {
    private Long id;
    private Date dob;
    private String address;
    private Integer loyaltyPoint;

    public CustomerProfileDTO() {
    }

    public CustomerProfileDTO(Long id, Date dob, String address, Integer loyaltyPoint) {
        this.id = id;
        this.dob = dob;
        this.address = address;
        this.loyaltyPoint = loyaltyPoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public void setLoyaltyPoint(Integer loyaltyPoint) {
        this.loyaltyPoint = loyaltyPoint;
    }

}

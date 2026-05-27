/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.dto;
/**
 *
 * @author ASUS
 */
public class CustomerProfileMeResponseDTO {
    private UserSummaryDTO user;
    private CustomerProfileDTO customerProfile;

    public CustomerProfileMeResponseDTO() {
    }

    public CustomerProfileMeResponseDTO(UserSummaryDTO user, CustomerProfileDTO customerProfile) {
        this.user = user;
        this.customerProfile = customerProfile;
    }

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }

    public CustomerProfileDTO getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfileDTO customerProfile) {
        this.customerProfile = customerProfile;
    }
}

package com.hvh.dto;

import java.util.Date;
import java.util.List;

public class ReservationRequestDTO {

    private Long id;
    private Date checkIn;
    private Date checkOut;
    private Long customerId;
    private List<Long> roomIds;
    private List<ServiceOrderRequestDTO> services;

    private String status;

    public ReservationRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<Long> roomIds) {
        this.roomIds = roomIds;
    }

    public List<ServiceOrderRequestDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceOrderRequestDTO> services) {
        this.services = services;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
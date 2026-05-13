package com.hvh.dto;

import java.math.BigDecimal;

public class ReservationRoomRequestDTO {

    private Long roomId;
    private BigDecimal pricePerNight;
    private String notes;

    public ReservationRoomRequestDTO() {}

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.dto;
/**
 *
 * @author 03358
 */

import java.math.BigDecimal;

public class ReservationRoomResponseDTO {
    private Long id;           
    private Long roomId;      
    private String roomName;
    private BigDecimal pricePerNight;
    private String notes;

    public ReservationRoomResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
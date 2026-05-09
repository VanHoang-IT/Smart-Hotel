package com.hvh.dto;

import java.util.Date;
import java.util.List;

public class ReservationRequestDTO {

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the checkIn
     */
    public Date getCheckIn() {
        return checkIn;
    }

    /**
     * @param checkIn the checkIn to set
     */
    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    /**
     * @return the checkOut
     */
    public Date getCheckOut() {
        return checkOut;
    }

    /**
     * @param checkOut the checkOut to set
     */
    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    /**
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the roomIds
     */
    public List<Long> getRoomIds() {
        return roomIds;
    }

    /**
     * @param roomIds the roomIds to set
     */
    public void setRoomIds(List<Long> roomIds) {
        this.roomIds = roomIds;
    }
    private Long id;
    private Date checkIn;
    private Date checkOut;
    private Long customerId;
    private List<Long> roomIds;
    private String status;
    public ReservationRequestDTO() {}

    // Getters and Setters
    
}
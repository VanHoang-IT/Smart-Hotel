package com.hvh.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ReservationDetailDTO {

    private Long id;
    private Date checkIn;
    private Date checkOut;
    private String status;
    private String customerName;
    private String createdByName;
    private Date createdAt;

    private List<RoomItem> rooms;
    private List<ServiceOrderItem> serviceOrders;
    private List<PaymentItem> payments;

    // --- Nested types ---
    public static class RoomItem {

        private Long id;
        private String roomName;
        private BigDecimal pricePerNight;

        public RoomItem(Long id, String roomName, BigDecimal pricePerNight) {
            this.id = id;
            this.roomName = roomName;
            this.pricePerNight = pricePerNight;
        }

        public Long getId() {
            return id;
        }

        public String getRoomName() {
            return roomName;
        }

        public BigDecimal getPricePerNight() {
            return pricePerNight;
        }
    }

    public static class ServiceOrderItem {

        private Long id;
        private String serviceName;
        private Integer qty;
        private BigDecimal unitPrice;
        private BigDecimal amount;
        private String status;
        private Date orderedAt;

        public ServiceOrderItem(Long id, String serviceName, Integer qty,
                BigDecimal unitPrice, BigDecimal amount, String status, Date orderedAt) {
            this.id = id;
            this.serviceName = serviceName;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.amount = amount;
            this.status = status;
            this.orderedAt = orderedAt;
        }

        public Long getId() {
            return id;
        }

        public String getServiceName() {
            return serviceName;
        }

        public Integer getQty() {
            return qty;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public Date getOrderedAt() {
            return orderedAt;
        }
    }

    public static class PaymentItem {

        private Long id;
        private BigDecimal totalAmount;
        private String method;
        private String status;
        private String transactionId;
        private Date paidAt;
        private Date createdAt;

        public PaymentItem(Long id, BigDecimal totalAmount, String method, String status,
                String transactionId, Date paidAt, Date createdAt) {
            this.id = id;
            this.totalAmount = totalAmount;
            this.method = method;
            this.status = status;
            this.transactionId = transactionId;
            this.paidAt = paidAt;
            this.createdAt = createdAt;
        }

        public Long getId() {
            return id;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public String getMethod() {
            return method;
        }

        public String getStatus() {
            return status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public Date getPaidAt() {
            return paidAt;
        }

        public Date getCreatedAt() {
            return createdAt;
        }
    }

    // --- Getters/Setters ---
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoomItem> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomItem> rooms) {
        this.rooms = rooms;
    }

    public List<ServiceOrderItem> getServiceOrders() {
        return serviceOrders;
    }

    public void setServiceOrders(List<ServiceOrderItem> serviceOrders) {
        this.serviceOrders = serviceOrders;
    }

    public List<PaymentItem> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentItem> payments) {
        this.payments = payments;
    }
}

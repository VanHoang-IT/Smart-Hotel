/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author 03358
 */
@Entity
@Table(name = "service_order")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceOrder.findAll", query = "SELECT s FROM ServiceOrder s"),
    @NamedQuery(name = "ServiceOrder.findById", query = "SELECT s FROM ServiceOrder s WHERE s.id = :id"),
    @NamedQuery(name = "ServiceOrder.findByQty", query = "SELECT s FROM ServiceOrder s WHERE s.qty = :qty"),
    @NamedQuery(name = "ServiceOrder.findByUnitPrice", query = "SELECT s FROM ServiceOrder s WHERE s.unitPrice = :unitPrice"),
    @NamedQuery(name = "ServiceOrder.findByAmount", query = "SELECT s FROM ServiceOrder s WHERE s.amount = :amount"),
    @NamedQuery(name = "ServiceOrder.findByOrderedAt", query = "SELECT s FROM ServiceOrder s WHERE s.orderedAt = :orderedAt")})
public class ServiceOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "qty")
    private Integer qty;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "ordered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderedAt;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Reservation reservationId;
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Service serviceId;

    public ServiceOrder() {
    }

    public ServiceOrder(Long id) {
        this.id = id;
    }

    public ServiceOrder(Long id, BigDecimal unitPrice, BigDecimal amount) {
        this.id = id;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(Date orderedAt) {
        this.orderedAt = orderedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Reservation getReservationId() {
        return reservationId;
    }

    public void setReservationId(Reservation reservationId) {
        this.reservationId = reservationId;
    }

    public Service getServiceId() {
        return serviceId;
    }

    public void setServiceId(Service serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceOrder)) {
            return false;
        }
        ServiceOrder other = (ServiceOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hvh.pojo.ServiceOrder[ id=" + id + " ]";
    }
    
}

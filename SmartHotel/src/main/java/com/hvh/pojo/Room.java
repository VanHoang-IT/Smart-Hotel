/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 03358
 */
@Entity
@Table(name = "room")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.id = :id"),
    @NamedQuery(name = "Room.findByRoomNumber", query = "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber"),
    @NamedQuery(name = "Room.findByFloor", query = "SELECT r FROM Room r WHERE r.floor = :floor"),
    @NamedQuery(name = "Room.findByStatus", query = "SELECT r FROM Room r WHERE r.status = :status"),
    @NamedQuery(name = "Room.findByMainImage", query = "SELECT r FROM Room r WHERE r.mainImage = :mainImage"),
    @NamedQuery(name = "Room.findByPrice", query = "SELECT r FROM Room r WHERE r.price = :price")})
    @JsonIgnoreProperties(value = {"housekeepingTaskSet", "roomImagesSet", "reservationRoomSet", "roomTypeId"})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "room_number")
    private String roomNumber;
    @Column(name = "floor")
    private Integer floor;
    @Size(max = 11)
    @Column(name = "status")
    private String status;
    @Size(max = 255)
    @Column(name = "main_image")
    private String mainImage;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Lob
    @Size(max = 65535)
    @Column(name = "note")
    private String note;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<ReservationRoom> reservationRoomSet;
    @JoinColumn(name = "room_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonProperty("roomType")
    private RoomType roomTypeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<HousekeepingTask> housekeepingTaskSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<RoomImages> roomImagesSet;

    @Transient
    private MultipartFile file;

    public Room() {
    }

    public Room(Long id) {
        this.id = id;
    }

    public Room(Long id, String roomNumber, BigDecimal price) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @XmlTransient
    public Set<ReservationRoom> getReservationRoomSet() {
        return reservationRoomSet;
    }

    public void setReservationRoomSet(Set<ReservationRoom> reservationRoomSet) {
        this.reservationRoomSet = reservationRoomSet;
    }

    public RoomType getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(RoomType roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @XmlTransient
    public Set<HousekeepingTask> getHousekeepingTaskSet() {
        return housekeepingTaskSet;
    }

    public void setHousekeepingTaskSet(Set<HousekeepingTask> housekeepingTaskSet) {
        this.housekeepingTaskSet = housekeepingTaskSet;
    }

    @XmlTransient
    public Set<RoomImages> getRoomImagesSet() {
        return roomImagesSet;
    }

    public void setRoomImagesSet(Set<RoomImages> roomImagesSet) {
        this.roomImagesSet = roomImagesSet;
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
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hvh.pojo.Room[ id=" + id + " ]";
    }

    /**
     * @return the file
     */
    
    public MultipartFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(MultipartFile file) {
        this.file = file;
    }

}

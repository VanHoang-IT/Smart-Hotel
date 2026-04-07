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
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 03358
 */
@Entity
@Table(name = "rooms")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rooms.findAll", query = "SELECT r FROM Rooms r"),
    @NamedQuery(name = "Rooms.findById", query = "SELECT r FROM Rooms r WHERE r.id = :id"),
    @NamedQuery(name = "Rooms.findByRoomNumber", query = "SELECT r FROM Rooms r WHERE r.roomNumber = :roomNumber"),
    @NamedQuery(name = "Rooms.findByFloor", query = "SELECT r FROM Rooms r WHERE r.floor = :floor"),
    @NamedQuery(name = "Rooms.findByStatus", query = "SELECT r FROM Rooms r WHERE r.status = :status"),
    @NamedQuery(name = "Rooms.findByMainImage", query = "SELECT r FROM Rooms r WHERE r.mainImage = :mainImage")})
@JsonIgnoreProperties(value = {"reservationRoomsSet", "housekeepingTasksSet", "roomImageSet"})
public class Rooms implements Serializable {

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
    @Lob
    @Size(max = 65535)
    @Column(name = "note")
    private String note;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<ReservationRooms> reservationRoomsSet;
    @JoinColumn(name = "room_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonProperty("roomType")
    private RoomTypes roomTypeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<HousekeepingTasks> housekeepingTasksSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomId")
    private Set<RoomImage> roomImageSet;
    
    @Transient
    private MultipartFile file;
    
    public Rooms() {
    }

    public Rooms(Long id) {
        this.id = id;
    }

    public Rooms(Long id, String roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @XmlTransient
    public Set<ReservationRooms> getReservationRoomsSet() {
        return reservationRoomsSet;
    }

    public void setReservationRoomsSet(Set<ReservationRooms> reservationRoomsSet) {
        this.reservationRoomsSet = reservationRoomsSet;
    }

    public RoomTypes getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(RoomTypes roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @XmlTransient
    public Set<HousekeepingTasks> getHousekeepingTasksSet() {
        return housekeepingTasksSet;
    }

    public void setHousekeepingTasksSet(Set<HousekeepingTasks> housekeepingTasksSet) {
        this.housekeepingTasksSet = housekeepingTasksSet;
    }

    @XmlTransient
    public Set<RoomImage> getRoomImageSet() {
        return roomImageSet;
    }

    public void setRoomImageSet(Set<RoomImage> roomImageSet) {
        this.roomImageSet = roomImageSet;
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
        if (!(object instanceof Rooms)) {
            return false;
        }
        Rooms other = (Rooms) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hvh.pojo.Rooms[ id=" + id + " ]";
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

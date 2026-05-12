package com.hvh.service.impl;

import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.dto.ServiceOrderRequestDTO;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.Room;
import com.hvh.pojo.ServiceOrder;
import com.hvh.pojo.Services;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.ReservationRoomRepository;
import com.hvh.repository.RoomRepository;
import com.hvh.repository.ServiceOrderRepository;
import com.hvh.repository.ServiceRepository;
import com.hvh.service.ReservationService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ServiceOrderRepository serOrderRepo;

    @Autowired
    private ServiceRepository serviceRepo;
    @Autowired
    private ReservationRepository resRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private ReservationRoomRepository resRoomRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservations(Map<String, String> params) {
        List<Reservation> reservations = this.resRepo.getReservations(params);
        return reservations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(long id) {
        Reservation res = this.resRepo.getReservationById(id);
        return (res != null) ? toResponseDTO(res) : null;
    }

    @Override
    @Transactional
    public ReservationResponseDTO createReservation(
            ReservationRequestDTO dto
    ) {

        Reservation res = new Reservation();

        res.setCreatedAt(new Date());

        res.setStatus("PENDING");

        mapDtoToEntity(dto, res);

        this.resRepo.addOrUpdateReservation(res);
        if (dto.getRoomIds() != null
                && !dto.getRoomIds().isEmpty()) {

            for (Long roomId : dto.getRoomIds()) {

                Room room = this.roomRepo.getRoomById(roomId);

                if (room == null) {
                    throw new RuntimeException(
                            "Room not found with id: " + roomId
                    );
                }

                ReservationRoom rr = new ReservationRoom();

                rr.setReservationId(res);

                rr.setRoomId(room);

                rr.setPricePerNight(room.getPrice());

                this.resRoomRepo.addOrUpdateReservationRoom(rr);
            }
        }

        if (dto.getServices() != null
                && !dto.getServices().isEmpty()) {

            for (ServiceOrderRequestDTO s : dto.getServices()) {

                Services service
                        = this.serviceRepo.getServiceById(
                                s.getServiceId()
                        );

                if (service == null) {
                    throw new RuntimeException(
                            "Service not found with id: "
                            + s.getServiceId()
                    );
                }

                ServiceOrder order = new ServiceOrder();

                order.setReservationId(res);

                order.setServiceId(service);

                order.setQty(s.getQty());

                order.setNotes(s.getNotes());

                order.setOrderedAt(new Date());

                order.setStatus("PENDING");

                order.setUnitPrice(service.getPrice());

                order.setAmount(
                        service.getPrice().multiply(
                                BigDecimal.valueOf(
                                        s.getQty()
                                )
                        )
                );

                this.serOrderRepo.addOrUpdate(order);
            }
        }
        return toResponseDTO(res);
    }

    @Override
    @Transactional
    public ReservationResponseDTO updateReservation(ReservationRequestDTO dto
    ) {
        if (dto.getId() == null) {
            throw new RuntimeException("ID đơn hàng không được để trống khi cập nhật");
        }
        Reservation res = this.resRepo.getReservationById(dto.getId());
        if (res == null) {
            throw new RuntimeException("Không tìm thấy đơn đặt phòng với ID: " + dto.getId());
        }
        mapDtoToEntity(dto, res);

        this.resRepo.addOrUpdateReservation(res);

        return toResponseDTO(res);
    }

    private ReservationResponseDTO toResponseDTO(Reservation res) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(res.getId());
        dto.setCheckIn(res.getCheckIn());
        dto.setCheckOut(res.getCheckOut());
        dto.setStatus(res.getStatus());

        if (res.getCustomerId() != null && res.getCustomerId().getUserId() != null) {
            dto.setCustomerName(res.getCustomerId().getUserId().getFullName());
        }

        if (res.getCreatedBy() != null) {
            dto.setCreatedByName(res.getCreatedBy().getUsername());
        }

        return dto;
    }

    private void mapDtoToEntity(ReservationRequestDTO dto, Reservation res) {
        if (dto.getCheckIn() != null) {
            res.setCheckIn(dto.getCheckIn());
        }
        if (dto.getCheckOut() != null) {
            res.setCheckOut(dto.getCheckOut());
        }
        if (dto.getStatus() != null) {
            res.setStatus(dto.getStatus());
        }
    }

    @Override
    @Transactional
    public ReservationResponseDTO cancelReservation(long id) {

        Reservation res = this.resRepo.getReservationById(id);

        if (res == null) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        res.setStatus("CANCELLED");

        this.resRepo.addOrUpdateReservation(res);

        return toResponseDTO(res);
    }
}

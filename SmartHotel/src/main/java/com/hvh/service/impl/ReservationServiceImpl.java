package com.hvh.service.impl;

import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.User;
import com.hvh.repository.CustomerRepository;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.UserRepository;
import com.hvh.service.ReservationService;
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
    private ReservationRepository resRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomerRepository customerRepo;

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
    public Reservation addOrUpdateReservation(ReservationRequestDTO dto) {
        Reservation savedReservation = null;
        if (dto.getId() != null) {
            Reservation res = this.resRepo.getReservationById(dto.getId());
            if (dto.getCheckIn() != null) res.setCheckIn(dto.getCheckIn());
            if (dto.getCheckOut() != null) res.setCheckOut(dto.getCheckOut());
            if (dto.getStatus() != null) res.setStatus(dto.getStatus());
            this.resRepo.addOrUpdateReservation(res);
            savedReservation = res;
        } else {
            User user = this.userRepo.getUserById(dto.getCustomerId());
            
            if (user != null) {
                CustomerProfile profile = user.getCustomerProfile();
                
                if (profile == null) {
                    profile = new CustomerProfile();
                    profile.setUserId(user);
                    profile.setLoyaltyPoint(0);
                    this.customerRepo.addCustomerProfile(profile);
                }
                
                dto.setCustomerId(profile.getId());
            }
            
            savedReservation = this.resRepo.createReservation(dto);
        } 
        
        return savedReservation;
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
}
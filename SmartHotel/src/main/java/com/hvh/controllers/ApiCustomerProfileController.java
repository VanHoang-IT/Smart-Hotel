package com.hvh.controllers;

import com.hvh.dto.CustomerProfileCreateRequestDTO;
import com.hvh.dto.CustomerProfileDTO;
import com.hvh.dto.CustomerProfileMeResponseDTO;
import com.hvh.dto.UserSummaryDTO;
import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.User;
import com.hvh.service.CustomerService;
import com.hvh.service.UserService;
import java.security.Principal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure/customer-profile")
@CrossOrigin
public class ApiCustomerProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerProfileMeResponseDTO> getMyCustomerProfile(Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        CustomerProfile profile = this.customerService.getCustomerByUserId(currentUser.getId());

        UserSummaryDTO userDto = new UserSummaryDTO(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getFullName(),
                currentUser.getEmail(),
                currentUser.getPhone()
        );

        CustomerProfileDTO profileDto = toCustomerProfileDto(profile);
        return new ResponseEntity<>(new CustomerProfileMeResponseDTO(userDto, profileDto), HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createCustomerProfile(
            @RequestBody CustomerProfileCreateRequestDTO request,
            Principal principal) {

        if (request.getDob() == null || request.getDob().isBlank()
                || request.getAddress() == null || request.getAddress().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("code", "INVALID_CUSTOMER_PROFILE_DATA");
            error.put("message", "dob and address are required");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        User currentUser = this.userService.getUserByUsername(principal.getName());

        try {
            CustomerProfile profileData = new CustomerProfile();
            profileData.setDob(Date.valueOf(request.getDob()));
            profileData.setAddress(request.getAddress().trim());

            CustomerProfile created = this.customerService.createCustomerProfile(currentUser.getId(), profileData);
            return new ResponseEntity<>(toCustomerProfileDto(created), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("code", "INVALID_CUSTOMER_PROFILE_DATA");
            error.put("message", "dob must be in yyyy-MM-dd format");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("code", "CUSTOMER_PROFILE_ALREADY_EXISTS");
            error.put("message", "Customer profile already exists");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }

    private CustomerProfileDTO toCustomerProfileDto(CustomerProfile profile) {
        if (profile == null) {
            return null;
        }
        return new CustomerProfileDTO(
                profile.getId(),
                profile.getDob(),
                profile.getAddress(),
                profile.getLoyaltyPoint()
        );
    }
}

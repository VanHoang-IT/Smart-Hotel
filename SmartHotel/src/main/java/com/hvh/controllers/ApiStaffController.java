/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;
/**
 *
 * @author ASUS
 */
import com.hvh.pojo.User;
import com.hvh.service.HousekeepingTaskService;
import com.hvh.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure/staff")
@CrossOrigin
@PreAuthorize("hasAuthority('ROLE_STAFF')")
public class ApiStaffController {

    @Autowired
    private HousekeepingTaskService housekeepingTaskService;
    @Autowired
    private UserService userService;

    @GetMapping("/housekeeping/my-tasks")
    public ResponseEntity<List<Map<String, Object>>> getMyTasks(Authentication auth) {
        User currentUser = this.userService.getUserByUsername(auth.getName());
        return new ResponseEntity<>(
                this.housekeepingTaskService.getTasksByAssignee(currentUser.getId()),
                HttpStatus.OK
        );
    }

    @PatchMapping("/housekeeping/{id}/status")
    public ResponseEntity<?> updateMyTaskStatus(
            @PathVariable("id") Long taskId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User currentUser = this.userService.getUserByUsername(auth.getName());
        this.housekeepingTaskService.updateStatusByAssignee(taskId, currentUser.getId(), status);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

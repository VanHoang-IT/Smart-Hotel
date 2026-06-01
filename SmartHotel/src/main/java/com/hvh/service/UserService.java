/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author 03358
 */
public interface UserService extends UserDetailsService{
    User getUserByUsername(String username) ;
    User addUser(Map<String, String> params, MultipartFile avatar);
    boolean authenticate(String username, String password);
    List<User> getUsers();
    List<User> getUsers(int page);
    void updateRole(Long id, String role);
}
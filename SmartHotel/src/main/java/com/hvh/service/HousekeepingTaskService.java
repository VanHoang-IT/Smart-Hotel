/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;
/**
 *
 * @author ASUS
 */
import com.hvh.pojo.HousekeepingTask;
import java.util.List;
import java.util.Map;

public interface HousekeepingTaskService {
    List<Map<String, Object>> getAll();
    List<Map<String, Object>> getAll(int page);
    List<Map<String, Object>> getTasksByAssignee(Long assigneeId);
    void addOrUpdate(Map<String, Object> payload);
    void updateStatus(Long id, String status);
    void updateStatusByAssignee(Long taskId, Long assigneeId, String status);
    void delete(Long id);
}
package com.hvh.service;

import com.hvh.pojo.HousekeepingTask;
import java.util.List;
import java.util.Map;

public interface HousekeepingTaskService {
    List<Map<String, Object>> getAll();
    void addOrUpdate(Map<String, Object> payload);
    void updateStatus(Long id, String status);
    void delete(Long id);
}

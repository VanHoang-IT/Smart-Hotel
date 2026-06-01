/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;
/**
 *
 * @author 03358
 */
import com.hvh.pojo.HousekeepingTask;
import java.util.List;

public interface HousekeepingTaskRepository {
    List<HousekeepingTask> getAll();
    List<HousekeepingTask> getAll(int page);
    List<HousekeepingTask> getByAssigneeId(Long assigneeId);
    HousekeepingTask getById(Long id);
    void addOrUpdate(HousekeepingTask task);
    void delete(Long id);
}
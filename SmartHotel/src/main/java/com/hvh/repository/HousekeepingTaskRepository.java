package com.hvh.repository;

import com.hvh.pojo.HousekeepingTask;
import java.util.List;

public interface HousekeepingTaskRepository {
    List<HousekeepingTask> getAll();
    HousekeepingTask getById(Long id);
    void addOrUpdate(HousekeepingTask task);
    void delete(Long id);
}

package com.hvh.service.impl;

import com.hvh.pojo.HousekeepingTask;
import com.hvh.pojo.Room;
import com.hvh.pojo.User;
import com.hvh.repository.HousekeepingTaskRepository;
import com.hvh.repository.RoomRepository;
import com.hvh.repository.UserRepository;
import com.hvh.service.HousekeepingTaskService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HousekeepingTaskServiceImpl implements HousekeepingTaskService {

    @Autowired
    private HousekeepingTaskRepository taskRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAll() {
        List<HousekeepingTask> tasks = this.taskRepo.getAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (HousekeepingTask t : tasks) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("task", t.getTask());
            m.put("status", t.getStatus());
            m.put("dueTime", t.getDueTime());
            m.put("notes", t.getNotes());
            m.put("createdAt", t.getCreatedAt());
            if (t.getRoomId() != null) {
                Map<String, Object> room = new HashMap<>();
                room.put("id", t.getRoomId().getId());
                room.put("name", t.getRoomId().getName());
                m.put("room", room);
            }
            if (t.getAssigneeId() != null) {
                Map<String, Object> assignee = new HashMap<>();
                assignee.put("id", t.getAssigneeId().getId());
                assignee.put("username", t.getAssigneeId().getUsername());
                assignee.put("fullName", t.getAssigneeId().getFullName());
                m.put("assignee", assignee);
            }
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional
    public void addOrUpdate(Map<String, Object> payload) {
        HousekeepingTask task = new HousekeepingTask();

        if (payload.get("id") != null) {
            task = this.taskRepo.getById(Long.valueOf(payload.get("id").toString()));
        }

        task.setTask(payload.get("task").toString());
        task.setStatus(payload.getOrDefault("status", "TODO").toString());
        task.setNotes(payload.get("notes") != null ? payload.get("notes").toString() : null);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());

        if (payload.get("dueTime") != null) {
            try {
                task.setDueTime(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                        .parse(payload.get("dueTime").toString()));
            } catch (Exception e) { /* ignore */ }
        }

        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Room room = this.roomRepo.getRoomById(roomId);
        task.setRoomId(room);

        if (payload.get("assigneeId") != null) {
            Long assigneeId = Long.valueOf(payload.get("assigneeId").toString());
            User assignee = this.userRepo.getUserById(assigneeId);
            task.setAssigneeId(assignee);
        }

        this.taskRepo.addOrUpdate(task);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        HousekeepingTask task = this.taskRepo.getById(id);
        if (task == null) throw new RuntimeException("Không tìm thấy task: " + id);
        task.setStatus(status);
        task.setUpdatedAt(new Date());
        this.taskRepo.addOrUpdate(task);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.taskRepo.delete(id);
    }
}

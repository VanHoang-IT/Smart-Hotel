/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.Room;
import com.hvh.pojo.RoomType;
import com.hvh.repository.RoomRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.hibernate.query.Query;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 03358
 */
@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    private Environment env;

    private static final int PAGE_SIZE = 6;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Room> getRoom(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Room> q = b.createQuery(Room.class);
        Root root = q.from(Room.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("name"), String.format("%%%s%%", kw)));
            }

            String fromPrice = params.get("fromPrice");
            if (fromPrice != null && !fromPrice.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("price"), fromPrice));
            }

            String toPrice = params.get("toPrice");
            if (toPrice != null && !toPrice.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(root.get("price"), toPrice));
            }

            String typeId = params.get("typeId");
            if (typeId != null && !typeId.isEmpty()) {
                predicates.add(b.equal(root.get("roomTypeId").get("id"), Integer.parseInt(typeId)));
            }

            String status = params.get("status");
            if (status != null && !status.isBlank()) {
                predicates.add(b.equal(root.get("status"), status));
            }
            
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.asc(root.get("id")));

        Query query = session.createQuery(q);

        if (params != null) {
            int pageSize = this.env.getProperty("rooms.page_size", Integer.class);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }
        return query.getResultList();
    }

    @Override
    public void addOrUpdateRoom(Room r) {
        Session session = this.factory.getObject().getCurrentSession();
        if (r.getRoomTypeId() != null && r.getRoomTypeId().getId() != null) {
            r.setRoomTypeId(session.getReference(RoomType.class, r.getRoomTypeId().getId()));
        }
        if (r.getId() != null) {
            session.merge(r);
        } else {
            session.persist(r);
        }
    }

    @Override
    public Room getRoomById(long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Room.class, id);
    }

    @Override
    public void deleteRoom(long id) {
        Session session = this.factory.getObject().getCurrentSession();
        Room r = this.getRoomById(id);
        session.remove(r);
    }

    @Override
    public List<Room> getRoomAvailable(String checkIn, String checkOut) {
        Session session = this.factory.getObject().getCurrentSession();

        if (checkIn == null || checkIn.isEmpty() || checkOut == null || checkOut.isEmpty()) {
            CriteriaBuilder b = session.getCriteriaBuilder();
            CriteriaQuery<Room> q = b.createQuery(Room.class);
            Root<Room> root = q.from(Room.class);
            q.where(b.equal(root.get("status"), "AVAILABLE"));
            return session.createQuery(q).getResultList();
        }

        try {
            Date checkInDate = Date.valueOf(checkIn);
            Date checkOutDate = Date.valueOf(checkOut);

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> blockedQuery = cb.createQuery(Long.class);
            Root<ReservationRoom> rr = blockedQuery.from(ReservationRoom.class);
            blockedQuery.select(rr.get("roomId").get("id")).distinct(true)
                    .where(
                            rr.get("reservationId").get("status").in("PENDING", "CONFIRMED", "CHECKED_IN"),
                            cb.lessThan(rr.get("reservationId").get("checkIn"), checkOutDate),
                            cb.greaterThan(rr.get("reservationId").get("checkOut"), checkInDate)
                    );
            List<Long> blockedIds = session.createQuery(blockedQuery).getResultList();

            CriteriaBuilder b = session.getCriteriaBuilder();
            CriteriaQuery<Room> q = b.createQuery(Room.class);
            Root<Room> root = q.from(Room.class);

            if (blockedIds.isEmpty()) {
                return session.createQuery(q).getResultList();
            } else {
                q.where(root.get("id").in(blockedIds).not());
                return session.createQuery(q).getResultList();
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kiểm tra phòng trống: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getRoomBookings(int roomId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<ReservationRoom> root = q.from(ReservationRoom.class);
        q.multiselect(
                root.get("reservationId").get("checkIn"),
                root.get("reservationId").get("checkOut")
        ).where(
                b.equal(root.get("roomId").get("id"), roomId),
                root.get("reservationId").get("status").in("PENDING", "CONFIRMED", "CHECKED_IN")
        );
        List<Object[]> rows = session.createQuery(q).getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> booking = new HashMap<>();
            booking.put("checkIn", row[0]);
            booking.put("checkOut", row[1]);
            result.add(booking);
        }
        return result;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.ReservationRoom;
import com.hvh.repository.ReservationRoomRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReservationRoomRepositoryImpl
        implements ReservationRoomRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public List<ReservationRoom> getReservationRooms(
            Map<String, String> params
    ) {

        Session session = this.factory
                .getObject()
                .getCurrentSession();

        CriteriaBuilder b = session.getCriteriaBuilder();

        CriteriaQuery<ReservationRoom> q =
                b.createQuery(ReservationRoom.class);

        Root root = q.from(ReservationRoom.class);

        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            String reservationId = params.get("reservationId");

            if (reservationId != null
                    && !reservationId.isEmpty()) {

                predicates.add(
                        b.equal(
                                root.get("reservationId")
                                        .get("id"),
                                Long.parseLong(reservationId)
                        )
                );
            }

            String roomId = params.get("roomId");

            if (roomId != null
                    && !roomId.isEmpty()) {

                predicates.add(
                        b.equal(
                                root.get("roomId")
                                        .get("id"),
                                Long.parseLong(roomId)
                        )
                );
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.desc(root.get("id")));

        Query query = session.createQuery(q);

        return query.getResultList();
    }

    @Override
    public void addOrUpdateReservationRoom(
            ReservationRoom rr
    ) {

        Session session = this.factory
                .getObject()
                .getCurrentSession();

        if (rr.getId() != null) {
            session.merge(rr);
        } else {
            session.persist(rr);
        }
    }

    @Override
    public ReservationRoom getReservationRoomById(
            long id
    ) {

        Session session = this.factory
                .getObject()
                .getCurrentSession();

        return session.get(
                ReservationRoom.class,
                id
        );
    }
}

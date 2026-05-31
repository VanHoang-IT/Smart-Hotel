/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Reservation;
import com.hvh.pojo.Review;
import com.hvh.repository.ReviewRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 *
 * @author 03358
 */
@Transactional
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Review> getReviews(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Review> q = b.createQuery(Review.class);
        Root<Review> root = q.from(Review.class);

        Fetch<Review, Reservation> resFetch = root.fetch("reservationId", JoinType.LEFT);
        resFetch.fetch("customerId", JoinType.LEFT).fetch("userId", JoinType.LEFT);
        resFetch.fetch("roomId", JoinType.LEFT);

        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String roomId = params.get("roomId");
            if (roomId != null && !roomId.isEmpty()) {
                predicates.add(b.equal(root.get("reservationId").get("roomId").get("id"), Long.parseLong(roomId)));
            }

            String rating = params.get("rating");
            if (rating != null && !rating.isEmpty()) {
                predicates.add(b.equal(root.get("rating"), Integer.parseInt(rating)));
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.desc(root.get("createdAt")));

        Query<Review> query = session.createQuery(q);

        if (params != null) {
            int pageSize = this.env.getProperty("review.page_size", Integer.class, 10);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }

        return query.getResultList();
    }

    @Override
    public List<Review> getReviewsByRoomId(Long roomId) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT r FROM Review r "
                + "JOIN r.reservationId res "
                + "JOIN res.reservationRoomSet rr "
                + "WHERE rr.roomId.id = :id "
                + "AND r.visible = true "
                + "ORDER BY r.createdAt DESC";

        Query<Review> q = s.createQuery(hql, Review.class);
        q.setParameter("id", roomId);

        return q.getResultList();
    }

    @Override
    public void addReviewOrUpdate(Review r
    ) {
        Session s = this.factory.getObject().getCurrentSession();
        if (r.getId() != null) {
            s.merge(r);
        } else {
            s.persist(r);
        }
    }

    @Override
    public Review getReviewById(Long id
    ) {
        return this.factory.getObject().getCurrentSession().get(Review.class, id);
    }

    @Override
    public void deleteReview(Long id
    ) {
        Session s = this.factory.getObject().getCurrentSession();
        Review r = this.getReviewById(id);
        if (r != null) {
            s.remove(r);
        }
    }

    @Override
    public boolean existsByReservationId(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT COUNT(r.id) "
           + "FROM Review r "
           + "WHERE r.reservationId.id = :reservationId";

        Long count = s.createQuery(hql, Long.class)
                .setParameter("reservationId", reservationId)
                .getSingleResult();

        return count > 0;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.ServiceOrder;
import com.hvh.repository.StatisticRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
public class StatisticRepositoryImpl implements StatisticRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Object[]> getMonthlyRevenue() {
        return getMonthlyRevenue(0);
    }

    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Payment> root = cq.from(Payment.class);
        Expression<Integer> monthExpr = cb.function("MONTH", Integer.class, root.get("createdAt"));
        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get("createdAt"));
        Expression<BigDecimal> sumTotal = cb.sum(root.get("totalAmount"));
        cq.multiselect(monthExpr, yearExpr, sumTotal);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), "COMPLETED"));
        if (year > 0) predicates.add(cb.equal(yearExpr, year));
        cq.where(predicates.toArray(Predicate[]::new));
        cq.groupBy(yearExpr, monthExpr);
        cq.orderBy(cb.desc(yearExpr), cb.desc(monthExpr));
        return session.createQuery(cq).getResultList();
    }

    @Override
    public Map<String, Object> getSummary() {
        return getSummary(0);
    }

    @Override
    public Map<String, Object> getSummary(int year) {
        Session session = this.factory.getObject().getCurrentSession();
        Map<String, Object> summary = new HashMap<>();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        if (year > 0) {
            CriteriaQuery<Long> countRes = cb.createQuery(Long.class);
            Root<Reservation> resRoot = countRes.from(Reservation.class);
            Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, resRoot.get("checkIn"));
            countRes.select(cb.count(resRoot)).where(cb.equal(yearExpr, year));
            summary.put("totalReservations", session.createQuery(countRes).getSingleResult());

            CriteriaQuery<BigDecimal> sumRev = cb.createQuery(BigDecimal.class);
            Root<Payment> payRoot = sumRev.from(Payment.class);
            Expression<Integer> payYear = cb.function("YEAR", Integer.class, payRoot.get("createdAt"));
            sumRev.select(cb.sum(payRoot.get("totalAmount")));
            sumRev.where(cb.equal(payRoot.get("status"), "COMPLETED"), cb.equal(payYear, year));
            BigDecimal total = session.createQuery(sumRev).getSingleResult();
            summary.put("totalRevenue", total != null ? total : BigDecimal.ZERO);

            CriteriaQuery<Long> countPending = cb.createQuery(Long.class);
            Root<Reservation> pr = countPending.from(Reservation.class);
            Expression<Integer> pYear = cb.function("YEAR", Integer.class, pr.get("checkIn"));
            countPending.select(cb.count(pr)).where(cb.equal(pr.get("status"), "PENDING"), cb.equal(pYear, year));
            summary.put("pendingReservations", session.createQuery(countPending).getSingleResult());

            CriteriaQuery<Long> countCI = cb.createQuery(Long.class);
            Root<Reservation> cir = countCI.from(Reservation.class);
            Expression<Integer> ciYear = cb.function("YEAR", Integer.class, cir.get("checkIn"));
            countCI.select(cb.count(cir)).where(cb.equal(cir.get("status"), "CHECKED_IN"), cb.equal(ciYear, year));
            summary.put("checkedInReservations", session.createQuery(countCI).getSingleResult());
        } else {
            CriteriaQuery<Long> countRes = cb.createQuery(Long.class);
            countRes.select(cb.count(countRes.from(Reservation.class)));
            summary.put("totalReservations", session.createQuery(countRes).getSingleResult());

            CriteriaQuery<BigDecimal> sumRev = cb.createQuery(BigDecimal.class);
            Root<Payment> payRoot = sumRev.from(Payment.class);
            sumRev.select(cb.sum(payRoot.get("totalAmount")));
            sumRev.where(cb.equal(payRoot.get("status"), "COMPLETED"));
            BigDecimal total = session.createQuery(sumRev).getSingleResult();
            summary.put("totalRevenue", total != null ? total : BigDecimal.ZERO);

            CriteriaQuery<Long> countPending = cb.createQuery(Long.class);
            Root<Reservation> pr = countPending.from(Reservation.class);
            countPending.select(cb.count(pr)).where(cb.equal(pr.get("status"), "PENDING"));
            summary.put("pendingReservations", session.createQuery(countPending).getSingleResult());

            CriteriaQuery<Long> countCI = cb.createQuery(Long.class);
            Root<Reservation> cir = countCI.from(Reservation.class);
            countCI.select(cb.count(cir)).where(cb.equal(cir.get("status"), "CHECKED_IN"));
            summary.put("checkedInReservations", session.createQuery(countCI).getSingleResult());
        }
        return summary;
    }

    @Override
    public List<Object[]> getTopRoomsByRevenue(int limit) {
        return getTopRoomsByRevenue(limit, 0);
    }

    @Override
    public List<Object[]> getTopRoomsByRevenue(int limit, int year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<ReservationRoom> root = cq.from(ReservationRoom.class);

        Expression<Integer> nights = cb.function("DATEDIFF", Integer.class,
            root.get("reservationId").get("checkOut"),
            root.get("reservationId").get("checkIn"));

        Expression<BigDecimal> roomRevenue = cb.prod(
            root.get("roomId").get("price").as(BigDecimal.class),
            nights.as(BigDecimal.class)
        );

        Expression<BigDecimal> sumRevenue = cb.sum(roomRevenue);

        cq.multiselect(root.get("roomId").get("name"), sumRevenue);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNotNull(root.get("reservationId").get("checkOut")));
        if (year > 0) {
            Expression<Integer> yearExpr = cb.function("YEAR", Integer.class,
                root.get("reservationId").get("checkIn"));
            predicates.add(cb.equal(yearExpr, year));
        }
        cq.where(predicates.toArray(Predicate[]::new));
        cq.groupBy(root.get("roomId").get("id"), root.get("roomId").get("name"),
                   root.get("roomId").get("price"));
        cq.orderBy(cb.desc(sumRevenue));
        return session.createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public List<Object[]> getTopServicesByRevenue(int limit) {
        return getTopServicesByRevenue(limit, 0);
    }

    @Override
    public List<Object[]> getTopServicesByRevenue(int limit, int year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<ServiceOrder> root = cq.from(ServiceOrder.class);

        Expression<BigDecimal> sumAmount = cb.sum(root.get("amount"));
        cq.multiselect(root.get("serviceId").get("name"), sumAmount);

        List<Predicate> predicates = new ArrayList<>();
        if (year > 0) {
            Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get("reservationId").get("checkIn"));
            predicates.add(cb.equal(yearExpr, year));
        }
        if (!predicates.isEmpty()) cq.where(predicates.toArray(Predicate[]::new));
        cq.groupBy(root.get("serviceId").get("id"), root.get("serviceId").get("name"));
        cq.orderBy(cb.desc(sumAmount));
        return session.createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public List<Object[]> getReservationsByStatus() {
        return getReservationsByStatus(0);
    }

    @Override
    public List<Object[]> getReservationsByStatus(int year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Reservation> root = cq.from(Reservation.class);
        cq.multiselect(root.get("status"), cb.count(root));
        if (year > 0) {
            Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get("checkIn"));
            cq.where(cb.equal(yearExpr, year));
        }
        cq.groupBy(root.get("status"));
        cq.orderBy(cb.desc(cb.count(root)));
        return session.createQuery(cq).getResultList();
    }

    @Override
    public List<Integer> getAvailableYears() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Payment> root = cq.from(Payment.class);
        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get("createdAt"));
        cq.select(yearExpr).distinct(true);
        cq.where(cb.equal(root.get("status"), "COMPLETED"));
        cq.orderBy(cb.desc(yearExpr));
        return session.createQuery(cq).getResultList();
    }
}
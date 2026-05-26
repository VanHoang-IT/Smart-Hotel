/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository.impl;

import com.hvh.pojo.Payment;
import com.hvh.repository.StatisticRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
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
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Payment> root = cq.from(Payment.class);
        Expression<Integer> monthExpr = cb.function("MONTH", Integer.class, root.get("createdAt"));
        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get("createdAt"));
        Expression<BigDecimal> sumTotalAmount = cb.sum(root.get("totalAmount"));

        cq.multiselect(monthExpr, yearExpr, sumTotalAmount);
        cq.where(cb.equal(root.get("status"), "COMPLETED"));
        cq.groupBy(yearExpr, monthExpr);
        cq.orderBy(cb.desc(yearExpr), cb.desc(monthExpr));

        return session.createQuery(cq).getResultList();
    }

}

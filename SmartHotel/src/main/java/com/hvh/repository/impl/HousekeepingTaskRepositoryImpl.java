/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hvh.repository.impl;
/**
 *
 * @author ASUS
 */
import com.hvh.pojo.HousekeepingTask;
import com.hvh.repository.HousekeepingTaskRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class HousekeepingTaskRepositoryImpl implements HousekeepingTaskRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<HousekeepingTask> getAll() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<HousekeepingTask> cq = cb.createQuery(HousekeepingTask.class);
        Root<HousekeepingTask> root = cq.from(HousekeepingTask.class);

        root.fetch("roomId");
        root.fetch("assigneeId");

        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("id")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public List<HousekeepingTask> getByAssigneeId(Long assigneeId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<HousekeepingTask> cq = cb.createQuery(HousekeepingTask.class);
        Root<HousekeepingTask> root = cq.from(HousekeepingTask.class);

        root.fetch("roomId");
        root.fetch("assigneeId");

        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("assigneeId").get("id"), assigneeId));
        cq.orderBy(cb.desc(root.get("updatedAt")), cb.desc(root.get("id")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public HousekeepingTask getById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(HousekeepingTask.class, id);
    }

    @Override
    public void addOrUpdate(HousekeepingTask task) {
        Session session = this.factory.getObject().getCurrentSession();
        if (task.getId() != null) {
            session.merge(task);
        } else {
            session.persist(task);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        HousekeepingTask task = getById(id);
        if (task != null) {
            session.remove(task);
        }
    }
}

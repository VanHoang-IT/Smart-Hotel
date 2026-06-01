package com.hvh.repository.impl;

import com.hvh.pojo.HousekeepingTask;
import com.hvh.repository.HousekeepingTaskRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class HousekeepingTaskRepositoryImpl implements HousekeepingTaskRepository {

    private static final int PAGE_SIZE = 10;

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
    public List<HousekeepingTask> getAll(int page) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<HousekeepingTask> cq = cb.createQuery(HousekeepingTask.class);
        Root<HousekeepingTask> root = cq.from(HousekeepingTask.class);
        root.fetch("roomId");
        root.fetch("assigneeId");
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("id")));
        int start = (page - 1) * PAGE_SIZE;
        Query query = session.createQuery(cq);
        query.setFirstResult(start);
        query.setMaxResults(PAGE_SIZE);
        return query.getResultList();
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
        if (task.getId() != null) session.merge(task);
        else session.persist(task);
    }

    @Override
    public void delete(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        HousekeepingTask task = getById(id);
        if (task != null) session.remove(task);
    }
}
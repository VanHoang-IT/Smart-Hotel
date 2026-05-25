package com.hvh.repository.impl;

import com.hvh.pojo.HousekeepingTask;
import com.hvh.repository.HousekeepingTaskRepository;
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
        return session.createQuery(
                "SELECT h FROM HousekeepingTask h LEFT JOIN FETCH h.roomId LEFT JOIN FETCH h.assigneeId ORDER BY h.id DESC",
                HousekeepingTask.class
        ).getResultList();
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

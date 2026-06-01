package com.hvh.repository.impl;

import com.hvh.pojo.User;
import com.hvh.repository.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).where(b.equal(root.get("username"), username));
        return session.createQuery(q).getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public User addUser(User u) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(u);
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);
        if (u == null) return false;
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public User getUserById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public List<User> getUsers() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).orderBy(b.asc(root.get("id")));
        return session.createQuery(q).getResultList();
    }

    @Override
    public List<User> getUsers(int page) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).orderBy(b.asc(root.get("id")));
        int start = (page - 1) * PAGE_SIZE;
        Query query = session.createQuery(q);
        query.setFirstResult(start);
        query.setMaxResults(PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public void updateUser(User u) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(u);
    }
}
package com.hvh.repository.impl;

import com.hvh.pojo.RoomImages;
import com.hvh.repository.RoomImagesRepository;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoomImagesRepositoryImpl implements RoomImagesRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<RoomImages> getByRoomId(Long roomId) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createQuery(
                "FROM RoomImages ri WHERE ri.roomId.id = :roomId", RoomImages.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    @Override
    public void addImage(RoomImages img) {
        Session session = this.factory.getObject().getCurrentSession();
        if (img.getRoomId() != null && img.getRoomId().getId() != null) {
            img.setRoomId(session.getReference(com.hvh.pojo.Room.class, img.getRoomId().getId()));
        }
        session.persist(img);
    }

    @Override
    public RoomImages getById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(RoomImages.class, id);
    }

    @Override
    public void delete(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        RoomImages img = this.getById(id);
        if (img != null) session.remove(img);
    }
}

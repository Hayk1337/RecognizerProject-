package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.model.UserInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findUser(String username) {
        return entityManager.find(User.class, username);
    }

    @Transactional
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Transactional
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Transactional
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    public int getAvailableSeconds(String username) {
        return entityManager.createQuery(
                        "SELECT availableSeconds FROM User WHERE username = :username", Integer.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Transactional
    public void decreaseAvailableSeconds(String username, int seconds) {
        entityManager.createQuery(
                        "UPDATE User SET availableSeconds = availableSeconds - :seconds WHERE username = :username")
                .setParameter("seconds", seconds)
                .setParameter("username", username)
                .executeUpdate();
    }

    public UserInfo getUserInfo(String username) {
        User user = findUser(username);
        if (user == null) {
            return null;
        }
        return new UserInfo(user);
    }
}

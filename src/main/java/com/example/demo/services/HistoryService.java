package com.example.demo.services;

import com.example.demo.entity.RecognizeHistoryEntity;
import com.example.demo.model.RecognizeHistoryItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void addHistoryItem(RecognizeHistoryEntity item) {
        entityManager.persist(item);
    }

    @Transactional
    public List<RecognizeHistoryItem> getUserRecognizeHistory(String username) {
        String jpql = "SELECT r FROM RecognizeHistoryEntity r WHERE r.user = :username ORDER BY r.id DESC";
        TypedQuery<RecognizeHistoryEntity> query = entityManager.createQuery(jpql, RecognizeHistoryEntity.class);
        query.setParameter("username", username);
        List<RecognizeHistoryEntity> entities = query.getResultList();
        return entities.stream()
                .map(entity -> {
                    RecognizeHistoryItem item = new RecognizeHistoryItem();
                    item.setId(entity.getId());
                    item.setName(entity.getName());
                    item.setRecognizeDate(entity.getRecognizeDate());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public String getHistoryItemResult(String id) {
       return entityManager.find(RecognizeHistoryEntity.class, id).getResult();
    }
}

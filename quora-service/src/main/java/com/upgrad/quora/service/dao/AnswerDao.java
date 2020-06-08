package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * create a answer
     * @param answerEntity
     * @return
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * edit answer
     * @param answerEntity
     * @return
     */
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    /**
     * get answer based on userId
     * @param questionId
     * @return
     */
    public AnswerEntity getAnswerByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("answerEntityByUuid", AnswerEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {

            return null;
        }
    }

    /**
     * get all answer based on questionId
     * @param questionId
     * @return
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("uuid", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * delete answer
     * @param answerId
     */
    public void userAnswerDelete(final String answerId) {
        AnswerEntity answerEntity = getAnswerByUuid(answerId);
        entityManager.remove(answerEntity);
    }
}

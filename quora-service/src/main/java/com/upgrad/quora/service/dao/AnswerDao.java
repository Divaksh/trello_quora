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
     * This method is to create an answer
     * @param answerEntity
     * @return AnswerEntity
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * This method is to edit the answer content.
     * @param answerEntity
     * @return AnswerEntity
     */
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    /**
     * This method is to get answer based on userId
     * @param questionId
     * @return AnswerEntity
     */
    public AnswerEntity getAnswerByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("answerEntityByUuid", AnswerEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {

            return null;
        }
    }

    /**
     * This method is to get all answer based on questionId
     * @param questionId
     * @return List of AnswerEntity
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("uuid", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method is to delete the answer by answerId
     * @param answerId
     */
    public void userAnswerDelete(final String answerId) {
        AnswerEntity answerEntity = getAnswerByUuid(answerId);
        entityManager.remove(answerEntity);
    }
}

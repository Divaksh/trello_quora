package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This class contain all Data access related operations for Question table
 */
@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is to create question
     * @param questionEntity
     * @return QuestionEntity
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * This method is to get all questions based on userId
     * @param uuid
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String uuid) {
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("uuid", uuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method is to get all questions
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }

    /**
     * This method is to update the question
     * @param questionEntity
     * @return QuestionEntity
     */
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    /**
     * This method is to delete the question
     * @param questionEntity
     */
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    /**
     * This method is to get the QuestionEntity by uuid
     *
     * @param questionId
     * @return QuestionEntity
     */

    public QuestionEntity getQuestionById(String questionId) {
        try {
            return entityManager.createNamedQuery("questionByQUuid", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

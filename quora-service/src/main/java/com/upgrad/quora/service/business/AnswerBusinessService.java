package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {
    @Autowired
    AnswerDao answerDao;

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    /**
     * @param answerEntity
     * @param questionId
     * @param authorization
     * @return AnswerEntity object is returned after persisting in the database.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String questionId, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        authorizeUser(userAuthEntity, "User is signed out.Sign in first to post an answer");
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUserEntity(userAuthEntity.getUserEntity());
        answerEntity.setQuestionEntity(questionEntity);

        return answerDao.createAnswer(answerEntity);
    }

    /**
     * @param answerEntity
     * @param authorization
     * @return AnswerEntity object is returned after persisting in the database.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        authorizeUser(userAuthEntity, "User is signed out.Sign in first to edit an answer");

        // Validate if requested answer exist or not
        AnswerEntity existingAnswerEntity = answerDao.getAnswerByUuid(answerEntity.getUuid());
        if (existingAnswerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Validate if current user is the owner of requested answer
        UserEntity currentUser = userAuthEntity.getUserEntity();
        UserEntity answerOwner = answerDao.getAnswerByUuid(answerEntity.getUuid()).getUserEntity();
        if (currentUser.getId() != answerOwner.getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setId(existingAnswerEntity.getId());
        answerEntity.setDate(existingAnswerEntity.getDate());
        answerEntity.setUserEntity(existingAnswerEntity.getUserEntity());
        answerEntity.setQuestionEntity(existingAnswerEntity.getQuestionEntity());
        return answerDao.editAnswerContent(answerEntity);
    }

    /**
     *
     * @param answerId
     * @param authorization
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String answerId, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        authorizeUser(userAuthEntity, "User is signed out.Sign in first to delete an answer");


        if (answerDao.getAnswerByUuid(answerId) == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }


        if (!userAuthEntity.getUserEntity().getUuid().equals(answerDao.getAnswerByUuid(answerId).getUserEntity().getUuid())) {
            if (userAuthEntity.getUserEntity().getRole().equals("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        }

        answerDao.userAnswerDelete(answerId);
    }

    /**
     *
     * @param questionId
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        authorizeUser(userAuthEntity, "User is signed out.Sign in first to get the answers");

        if (questionDao.getQuestionById(questionId) == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        return answerDao.getAllAnswersToQuestion(questionId);
    }

    /**
     *
     * @param userAuthEntity
     * @param log_out_ERROR
     * @throws AuthorizationFailedException
     */
    private void authorizeUser(UserAuthTokenEntity userAuthEntity, final String log_out_ERROR) throws AuthorizationFailedException {
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", log_out_ERROR);
        }
    }
}

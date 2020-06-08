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

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private QuestionDao questionDao;

  /**
   * This method is to create the question
   *
   * @param questionEntity
   * @param accessToken
   * @return QuestionEntity
   * @throws AuthorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity createQuestion(QuestionEntity questionEntity, String accessToken)
      throws AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
    authorizeUser(userAuthEntity, "User is signed out.Sign in first to post a question");

    questionEntity.setUserEntity(userAuthEntity.getUserEntity());
    return questionDao.createQuestion(questionEntity);
  }

  /**
   * This method is to get all the questions
   *
   * @param token
   * @return List of QuestionEntity
   * @throws AuthorizationFailedException
   */
  public List<QuestionEntity> getAllQuestions(String token) throws AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(token);
    authorizeUser(userAuthEntity, "User is signed out.Sign in first to get all questions");
    return questionDao.getAllQuestions();
  }

  /**
   * This method is to edit the question
   *
   * @param questionEntity
   * @param authorizationToken
   * @return QuestionEntity
   * @throws AuthorizationFailedException, InvalidQuestionException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity editQuestion(final QuestionEntity questionEntity,
      final String authorizationToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

    authorizeUser(userAuthEntity, "User is signed out.Sign in first to edit the question");

    QuestionEntity existingQuestionEntity = questionDao.getQuestionById(questionEntity.getUuid());
    if (existingQuestionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
    UserEntity currentUser = userAuthEntity.getUserEntity();
    UserEntity questionOwner = existingQuestionEntity.getUserEntity();
    if (currentUser.getId() != questionOwner.getId()) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner can edit the question");
    }
    questionEntity.setId(existingQuestionEntity.getId());
    questionEntity.setUserEntity(existingQuestionEntity.getUserEntity());
    questionEntity.setDate(existingQuestionEntity.getDate());

    return questionDao.updateQuestion(questionEntity);
  }

  /**
   * This method is to delete the question by questionId
   *
   * @param questionId
   * @param authorization
   * @throws InvalidQuestionException, AuthorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteQuestion(final String questionId, final String authorization)
      throws InvalidQuestionException, AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

    authorizeUser(userAuthEntity, "User is signed out.Sign in first to delete a question");
    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);

    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }

    if (userAuthEntity.getUserEntity().getRole().equals("nonadmin")
        && userAuthEntity.getUserEntity().getUuid() != questionEntity.getUserEntity().getUuid()) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner or admin can delete the question");
    }

    questionDao.deleteQuestion(questionEntity);
  }

  /**
   * This method is to get all the questions by userId.
   *
   * @param userId
   * @param authorizationToken
   * @return List of QuestionEntity
   * @throws AuthorizationFailedException, UserNotFoundException
   */
  public List<QuestionEntity> getAllQuestionsByUser(final String userId,
      final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

    authorizeUser(userAuthEntity,
        "User is signed out.Sign in first to get all questions posted by a specific user");
    // Validate if requested user exist or not
    if (userDao.getUser(userId) == null) {
      throw new UserNotFoundException("USR-001",
          "User with entered uuid whose question details are to be seen does not exist");
    }
    return questionDao.getAllQuestionsByUser(userId);
  }

  /**
   * This method is to authorize the user
   *
   * @param userAuthEntity
   * @param log_out_ERROR
   * @throws AuthorizationFailedException
   */
  private void authorizeUser(UserAuthTokenEntity userAuthEntity, final String log_out_ERROR)
      throws AuthorizationFailedException {
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", log_out_ERROR);
    }
  }

}

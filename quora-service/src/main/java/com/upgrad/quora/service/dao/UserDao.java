package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


/**
 * This class contain all Data access related operations for User table
 */

@Repository
public class UserDao {


  @PersistenceContext
  private EntityManager entityManager;   //instance of entity manager to interact with the quora db

  /**
   * This method is to create a new user and persist it to database.
   *
   * @param userEntity
   * @return userEntity
   */
  public UserEntity createUser(UserEntity userEntity) {
    entityManager.persist(userEntity);
    return userEntity;
  }

  /**
   * This method is to persist authentication details
   *
   * @param userAuthTokenEntity
   * @return userEntity
   */
  public UserAuthTokenEntity createAuthUser(UserAuthTokenEntity userAuthTokenEntity) {
    entityManager.persist(userAuthTokenEntity);
    return userAuthTokenEntity;
  }

  /**
   * This method is to get user by UUID
   *
   * @param userUuid
   * @return userEntity
   */
  public UserEntity getUser(final String userUuid) {
    try {
      return entityManager.createNamedQuery("userByUuid", UserEntity.class)
          .setParameter("uuid", userUuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method is to get user by Username
   *
   * @param userName
   * @return userEntity
   */
  public UserEntity getUserByUserName(final String userName) {
    try {
      return entityManager.createNamedQuery("userByUserName", UserEntity.class)
          .setParameter("userName", userName).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method is to get user by email
   *
   * @param email
   * @return userEntity
   */
  public UserEntity getUserByEmail(final String email) {
    try {
      return entityManager.createNamedQuery("userByEmail", UserEntity.class)
          .setParameter("email", email).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }


  /**
   * This method is to delete the user
   *
   * @param userEntity
   */
  public void deleteUser(final UserEntity userEntity) {
    entityManager.remove(userEntity);
  }

  // Get authentication token by acesss token

  /**
   * This method is to get authentication token by acesss token
   *
   * @param accessToken
   */
  public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
    try {
      return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
          .setParameter("accessToken", accessToken).getSingleResult();
    } catch (NoResultException nre) {

      return null;
    }

  }

  /**
   * This method is to persist the authentication token
   *
   * @param userAuthTokenEntity
   * @return userAuthTokenEntity
   */
  public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
    entityManager.persist(userAuthTokenEntity);
    return userAuthTokenEntity;
  }

  /**
   * This method is to update an existing user
   *
   * @param updatedUserEntity
   */
  public void updateUser(final UserEntity updatedUserEntity) {

    entityManager.merge(updatedUserEntity);
  }

}
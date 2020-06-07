package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {


    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return  userEntity;
    }

    public UserEntity getUserByEmail(final String email) {

        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity checkUsername(String userName) {

        try{
            return entityManager.createNamedQuery("checkUserName",UserEntity.class).setParameter("username",userName).getSingleResult();

        }catch(NoResultException nre){
            return null;
        }
    }

    public UserEntity checkEmailaddress(String emailAddress) {
        try{
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",emailAddress).getSingleResult();

        }catch(NoResultException nre){
            return null;
        }
    }
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }
    public UserAuthTokenEntity checkUserStatus(final String accessToken) {
        try{
            return entityManager.createNamedQuery("userStatusByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

    public void updateUserStatus(final UserAuthTokenEntity userSignedOut) {
        entityManager.merge(userSignedOut);

    }

}
package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


//A Repository class to process the api-endpoint's requests and transact with the the quora database
@Repository
public class UserDao {


    @PersistenceContext
    private EntityManager entityManager;   //instance of entity manager to interact with the quora db

    //method to persist or create a new userentity in the Users table
    //receives the userentity to be persisted
    //returns the userentity after persisting it in the db

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return  userEntity;
    }

    //method to validate whether a given username is present in the users table
    //receives the username to be checked
    //uses a namedquery to look for the given username
    //returns the user if found else returns null
    public UserEntity checkUsername(String userName) {

        try{
            return entityManager.createNamedQuery("checkUserName",UserEntity.class).setParameter("username",userName).getSingleResult();

        }catch(NoResultException nre){
            return null;
        }
    }

    //method to validate whether a given emailaddress is present in the users table
    //receives the emailaddress to be checked
    //uses a namedquery to look for the given emailaddress
    //returns the user if found else returns null
    public UserEntity checkEmailaddress(String emailAddress) {
        try{
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",emailAddress).getSingleResult();

        }catch(NoResultException nre){
            return null;
        }
    }

    //method to persist or create a UserAuthtoken entity in userAuthTokenEntity table
    //receives a userauthtoken and returns the same after persisting it in the db

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    //method to check the user status
    //receives the access token
    //executes a named query to look for the given userAuthtoken
    //if found returns it else returns null

    public UserAuthTokenEntity checkUserStatus(final String accessToken) {
        try{
            return entityManager.createNamedQuery("userStatusByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

    //method to update the userauthtoken when the user wants to signout of the application
    //updates the given userauthtoken by calling merge on entity manager
    //receives the userauthtoken to be updated

    public void updateUserStatus(final UserAuthTokenEntity userSignedOut) {
        entityManager.merge(userSignedOut);

    }

    //method to fetch the user details by the userid
    //receives the userId as a string
    //fetches the user details and returns it if found else returns null

    public UserEntity fetchUserDetails(final String userId) {
        try{
            return entityManager.createNamedQuery("userByUuid",UserEntity.class).setParameter("uuid",userId).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //method to delete a user from the users table in quora db
    //receives the userentity that is to be deleted
    //deletes the given userentity by calling the remove method on entity manager

    public void deleteUser(UserEntity fetchedUser) {
        entityManager.remove(fetchedUser);
    }
}

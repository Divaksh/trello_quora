package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//service class to process the Admincontroller requests
@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;                         //instance of userDao

    @Autowired
    private AuthorizationService authorizationService;              //instance of AuthorizationService


    //method to authenticate the deleteUser endpoint/request
    //receives the accestokenand the requestdetails string from admincontroller
    //calls the authorizationservice method to authenticate the user delete request
    //throws the authorizationFailedException and usernotfoundexception
    //returns the userauthtoken entity

    public UserAuthTokenEntity authenticateAdminRequest( final String accessToken,final String requestDetailsString) throws AuthenticationFailedException,UserNotFoundException {
        return authorizationService.authenticateAdminRequest(accessToken,requestDetailsString);
    }

    //method to process the deleteUser request after the same is authenticated
    //deletes the user corresponding to the userId received by this metod
    //interacts with the userDao methods to delete the concerned user details
    //returns the deleted user details
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userId) throws UserNotFoundException {
        UserEntity userToBeDeleted =userDao.fetchUserDetails(userId);
        userDao.deleteUser(userToBeDeleted);
        return userToBeDeleted;
    }
}

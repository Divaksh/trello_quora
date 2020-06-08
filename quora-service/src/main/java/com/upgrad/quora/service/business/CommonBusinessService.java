package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


// a service class for the CommonController
@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;    //instance of userDao

    @Autowired
    private AuthorizationService authorizationService;    //instance of AuthorizationService


    //a method to authenticate the user request
    //receives the accesstoken that validates the user status and the requestdetailsstring
    //returns the userauthtoken if no exception is thrown
    //throws the authenticationFailedexception and also the usernotfoundexception

    public UserAuthTokenEntity authenticateCommonRequest( final String accessToken,final String requestDetailsString) throws AuthenticationFailedException, UserNotFoundException {
        return authorizationService.authenticateCommonRequest(accessToken,requestDetailsString);
    }

    //method to fetch the user details
    //receives the userId and throws usernotfoundexception
    //returns the userdetails

    public UserEntity fetchUserDetails(final String userId) throws UserNotFoundException {
        return userDao.fetchUserDetails(userId);
    }
}

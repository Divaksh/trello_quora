package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorizationService authorizationService;

    public UserAuthTokenEntity authenticateAdminRequest( final String accessToken,final String requestDetailsString) throws AuthenticationFailedException,UserNotFoundException {
        return authorizationService.authenticateAdminRequest(accessToken,requestDetailsString);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userId) throws UserNotFoundException {
        UserEntity userToBeDeleted =userDao.fetchUserDetails(userId);
        userDao.deleteUser(userToBeDeleted);
        return userToBeDeleted;
    }
}

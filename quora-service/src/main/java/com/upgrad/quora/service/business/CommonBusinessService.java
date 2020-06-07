package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorizationService authorizationService;

    public UserAuthTokenEntity authenticateCommonRequest( final String accessToken,final String requestDetailsString) throws AuthenticationFailedException, UserNotFoundException {
        return authorizationService.authenticateCommonRequest(accessToken,requestDetailsString);
    }

    public UserEntity fetchUserDetails(final String userId) throws UserNotFoundException {
        return userDao.fetchUserDetails(userId);
    }
}

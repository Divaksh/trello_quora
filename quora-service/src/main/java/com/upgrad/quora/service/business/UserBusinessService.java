package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signupUser(UserEntity userEntity) {
        String[] encrypt = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encrypt[0]);
        userEntity.setPassword(encrypt[1]);
        return userDao.createUser(userEntity);
    }

    public void validateUserSignUpRequest(final String userName,final String emailAddress) throws SignUpRestrictedException{
        String requestDetails ="validate-username";
        authorizationService.validateUserSignUpRequest(userName,requestDetails);
        requestDetails="validate-emailaddress";
        authorizationService.validateUserSignUpRequest(emailAddress,requestDetails);
    }

}

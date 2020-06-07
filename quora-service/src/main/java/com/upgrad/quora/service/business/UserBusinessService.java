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
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username , final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.checkUsername(username);
        if(userEntity == null) {

            throw new AuthenticationFailedException("ATH-001","This Username does not exist");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUserEntity(userEntity);
            final ZonedDateTime now=ZonedDateTime.now();
            final ZonedDateTime expiresAt=now.plusHours(8);
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));

            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userDao.createAuthToken(userAuthTokenEntity);
            return userAuthTokenEntity;
        }
        else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }
}

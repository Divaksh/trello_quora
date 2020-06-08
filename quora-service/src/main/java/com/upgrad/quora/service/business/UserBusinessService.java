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

//a service class that provides the business logic for the UserController requests
@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;    //instance of UserDao

    @Autowired
    private AuthorizationService authorizationService;   //instance of AuthorizationService

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;      //instance of passwordcryptographyprovider

    //service method to handle the signUpUser endpoint/request
    //receives the user details entered by the user
    //encrypts the password entered by the user,generates a salt
    //sets the encrypted password and salt attributes of the user
    // and and passes on the userEntity to userDao to be persisted in the users table
    //returns the created userEntity

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signupUser(UserEntity userEntity) {
        String[] encrypt = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encrypt[0]);
        userEntity.setPassword(encrypt[1]);
        return userDao.createUser(userEntity);
    }

    //service method to handle the signUpUser endpoint/request
    //receives the username and emailaddress entered by the user during signup
    //validates the same by calling the appropriate method in authorization service
    //throws signuprestrictedexception

    public void validateUserSignUpRequest(final String userName,final String emailAddress) throws SignUpRestrictedException{
        String requestDetails ="validate-username";
        authorizationService.validateUserSignUpRequest(userName,requestDetails);
        requestDetails="validate-emailaddress";
        authorizationService.validateUserSignUpRequest(emailAddress,requestDetails);
    }
    //service method to help process the signin endpoint/request
    //receives the username and password from the signIn request
    //validates the uesrname and if valid then validates the password
    //if valid password,generates UserAuthToken and return the same after persisting the same in the db
    //throws the authenticationFailedException

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

    //service method to handle the signOut endpoint/request
    //receives the accesstoken from the controller class
    //validates the user logged in status
    //if valid,then updates the logged out time and updates the UserAuthToken
    //returns the token after committing it to the db

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticateUserRequest( final String accessToken) throws SignUpRestrictedException {
        String requestDetailsString="sign-out-request@ID " + "no-id-required";
        UserAuthTokenEntity userSignedIn= authorizationService.authenticateUserRequest(accessToken,requestDetailsString);
        userSignedIn.setLogoutAt(ZonedDateTime.now());
        userDao.updateUserStatus(userSignedIn);
        return userSignedIn;
    }
}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//a service class that provides the business logic for the UserController requests
@Service
public class UserBusinessService {

  @Autowired
  private AdminBusinessService adminBusinessService; //instance of AdminBusinessService

  @Autowired
  private UserDao userDao;    //instance of UserDao

  @Autowired
  private AuthenticationService authenticationService;

  //service method to handle the signUpUser endpoint/request
  //receives the user details entered by the user
  //encrypts the password entered by the user,generates a salt
  //sets the encrypted password and salt attributes of the user
  // and and passes on the userEntity to userDao to be persisted in the users table
  //returns the created userEntity

  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
    return adminBusinessService.createUser(userEntity);
  }


  //service method to handle the signOut endpoint/request
  //receives the accesstoken from the controller class
  //validates the user logged in status
  //if valid,then updates the logged out time and updates the UserAuthToken
  //returns the token after committing it to the db
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {
    return authenticationService.userLogout(authorizationToken);
  }


  // Service method to get the user details
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity getUser(final String userUuid, final String authorizationToken)
      throws AuthorizationFailedException,
      UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

    // Check if the user is signed in or not
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // Check if the user has signed out
    if (userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get user details");
    }

    UserEntity userEntity = userDao.getUser(userUuid);

    // Check if the user exists
    if (userEntity == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }

    return userEntity;
  }

}

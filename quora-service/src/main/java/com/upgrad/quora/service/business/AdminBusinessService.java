package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//service class to process the Admincontroller requests
@Service
public class AdminBusinessService {

  @Autowired
  private UserDao userDao; //instance of userDao

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  //method to authenticate the deleteUser endpoint/request
  //receives the accestokenand the requestdetails string from admincontroller
  //calls the authorizationservice method to authenticate the user delete request
  //throws the authorizationFailedException and usernotfoundexception
  //returns the userauthtoken entity

  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity deleteUser(final String userUuid, final String authorizationToken)
      throws AuthorizationFailedException,
      UserNotFoundException {
    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out");
    }
    if ("nonadmin".equals(userAuthTokenEntity.getUserEntity().getRole())) {
      throw new AuthorizationFailedException("ATHR-003",
          "Unauthorized Access, Entered user is not an admin");
    }
    UserEntity userEntity = userDao.getUser(userUuid);
    if (userEntity == null) {
      throw new UserNotFoundException("USR-001",
          "User with entered uuid to be deleted does not exist");
    }
    userDao.deleteUser(userEntity);
    return userEntity;
  }

  //method to process the createUser request
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

    UserEntity user = userDao.getUserByUserName(userEntity.getUserName());

    // Check if username already exists
    if (user != null) {
      throw new SignUpRestrictedException("SGR-001",
          "Try any other Username, this Username has already been taken");
    }

    // Check if email already exists
    UserEntity userEmail = userDao.getUserByEmail(userEntity.getEmail());

    if (userEmail != null) {
      throw new SignUpRestrictedException("SGR-002",
          "This user has already been registered, try with any other emailId");
    }

    //Encrypt password and add salt
    String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
    userEntity.setSalt(encryptedText[0]);
    userEntity.setPassword(encryptedText[1]);

    return userDao.createUser(userEntity);
  }
}

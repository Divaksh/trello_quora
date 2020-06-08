package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthenticationService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  /**
   * This method is to authenticate the user.
   *
   * @param username
   * @param password
   * @throws AuthenticationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public UserAuthTokenEntity authenticate(final String username, final String password)
      throws AuthenticationFailedException {
    UserEntity userEntity = userDao.getUserByUserName(username);

    // Check if user name does not exist
    if (userEntity == null) {
      throw new AuthenticationFailedException("ATH-001", "This username does not exist");
    }

    String encryptedPassword = PasswordCryptographyProvider.encrypt(password, userEntity.getSalt());

    //Check if password matches the password-salt stored in the database
    if (encryptedPassword.equals(userEntity.getPassword())) {
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
      UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
      userAuthToken.setUserEntity(userEntity);

      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(8);
      userAuthToken.setUuid(userEntity.getUuid());
      userAuthToken
          .setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

      userAuthToken.setLoginAt(now);
      userAuthToken.setExpiresAt(expiresAt);

      userDao.createAuthToken(userAuthToken);
      userDao.updateUser(userEntity);

      return userAuthToken;
    } else {
      throw new AuthenticationFailedException("ATH-002", "Password failed");
    }

  }

  /**
   * This method is for user logout
   *
   * @param authorizationToken
   * @throws SignOutRestrictedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity userLogout(final String authorizationToken) throws SignOutRestrictedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

    // throw exception SignOutRestrictedException if user is not signed in.
    if (userAuthEntity == null) {
      throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
    }

    final ZonedDateTime lastLoginTime = userAuthEntity.getLoginAt();
    final ZonedDateTime lastLogoutTime = userAuthEntity.getLogoutAt();

    // Multiple log out should not happen.
    if (lastLogoutTime != null && lastLogoutTime.isAfter(lastLoginTime)) {
      throw new SignOutRestrictedException("SGR-001", "User is not Signed in");

    }

    final ZonedDateTime now = ZonedDateTime.now();

    userAuthEntity.setLogoutAt(now);

    return userAuthEntity.getUserEntity();
  }


}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AuthorizationService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private AnswerDao answerDao;

    public void validateUserSignUpRequest(final String searchByString, final String requestDetails) throws SignUpRestrictedException {
        new UserEntity();
        UserEntity validateUserSignUp;
        switch (requestDetails) {

            case "validate-username":
                validateUserSignUp = userDao.checkUsername(searchByString);
                if (validateUserSignUp != null) {
                    throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
                }
                break;
            case "validate-emailaddress":
                validateUserSignUp = userDao.checkEmailaddress(searchByString);
                if (validateUserSignUp != null) {
                    throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
                }
                break;
            default:
                break;
        }
    }


    public UserAuthTokenEntity authenticateUserRequest(final String accessToken, String requestDetailsString) throws SignUpRestrictedException {
        UserRequestDetails userRequestDetails = getUserRequestDetails(accessToken, requestDetailsString);
        UserAuthTokenEntity userSignedIn = userRequestDetails.getUserAuthTokenEntity();
        String request = userRequestDetails.getRequest();
        boolean notSignedIn = userRequestDetails.isUserNotSignedIn();
        boolean signedOut = userRequestDetails.isUserSignedOut();
        if (request.equalsIgnoreCase("sign-out-request")) {
            if (notSignedIn || signedOut) {
                throw new SignUpRestrictedException("SGR-001", "User is not signed in");
            }
        }
        return userSignedIn;

    }
    public UserRequestDetails getUserRequestDetails(final String accessToken,final String requestDetailsString){
        String [] requestDetails =requestDetailsString.split("@ID ",2);
        String request=requestDetails[0];
        String searchById=requestDetails[1];
        UserAuthTokenEntity userSignedIn= userDao.checkUserStatus(accessToken);
        ZonedDateTime timeNow = ZonedDateTime.now();
        boolean userNotSignedIn,userSignedOut;
        userNotSignedIn=userSignedOut=false;
        if(userSignedIn==null) userNotSignedIn=true;
        if(((!userNotSignedIn) && (userSignedIn.getLogoutAt() != null)) || (!userNotSignedIn) && (timeNow.isAfter(userSignedIn.getExpiresAt()))) userSignedOut=true;
        return new UserRequestDetails(userSignedIn,request,searchById,userNotSignedIn,userSignedOut);

    }
}



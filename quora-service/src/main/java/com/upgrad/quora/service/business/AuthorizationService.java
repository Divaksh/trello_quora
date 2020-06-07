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
        switch(requestDetails) {

            case "validate-username":
                validateUserSignUp = userDao.checkUsername(searchByString);
                if(validateUserSignUp != null){
                    throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
                }
                break;
            case "validate-emailaddress":
                validateUserSignUp = userDao.checkEmailaddress(searchByString);
                if(validateUserSignUp != null){
                    throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
                }
                break;
            default:
                break;
        }
    }

}



package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {


    @Autowired
    private UserBusinessService userBusinessService;

    //controller method for handling the userSignUp endpoint/request
    //receives the SignupUserRequest object and returns the SignupUserResponse as a Response Entity
    //validates the username and email address entered by the user and creates a new UserEntity with info received if valid
    //creates the new UserEntity by passing on the user info to the UserBusinessService business logic layer
    //returns the appropriate message along with the id of the UserEntity created in the response entity,and also httpstatus


    @RequestMapping(method = RequestMethod.POST, path = "user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        userBusinessService.validateUserSignUpRequest(signupUserRequest.getUserName(),signupUserRequest.getEmailAddress());
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setRole("nonadmin");

        final UserEntity createdUserEntity = userBusinessService.signupUser(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    //controller method for the userSiginIn endpoint/request
    //receives the authorization as basic authorization,throws AuthenticationFailedException if invalid credentials provided
    //decodes the base64 encoded authorization string and validates the username and password provided by the user
    //successfully validates and then creates accesstoken for the user by accessing the userBusinessservice layer
    //returns the accesstoken and the appropriate message in the response entity

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = userBusinessService.authenticate(decodedArray[0], decodedArray[1]);

        UserEntity user = userAuthToken.getUserEntity();

        SigninResponse signinResponse = new SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<>(signinResponse, headers, HttpStatus.OK);
    }

    //controller method for the UserSignOut endpoint/request
    //receives accesstoken as a bearer authorization and returns response entity
    //authenticates user request-throws the signuprestrictedexception
    //if user is logged in then logs him out and sends the signoutresponse with the appropriate message and http status



    @RequestMapping(method = RequestMethod.POST , path="/user/signout" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization")  final String authorization) throws SignUpRestrictedException{
        String accessToken=authorization.split("Bearer ")[1];
        UserAuthTokenEntity userSignedIn =  userBusinessService.authenticateUserRequest(accessToken);
        UserEntity user = userSignedIn.getUserEntity();
        SignoutResponse signoutResponse = new SignoutResponse().id(user.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<>(signoutResponse, HttpStatus.OK);
    }
}

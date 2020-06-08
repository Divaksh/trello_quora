package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {


    @Autowired
    private UserBusinessService userBusinessService;

    //controller method for the getUserDetails endpoint
   //receives the userId of the user whose details are to be fetched along with accesstoken of the user trying to fetch them
   //this method is available to all the users of the application
   //validates the user logged in by authenticating the access token passed in the authorization
   //throws the authenticationFailedException if not user is not signed in
   //throws the usernotfoundException if the user with the given userId is not found in the db
   //returns the user details if no exception thrown in the response entity after creating a userdetailsresponse object
   //along with the http status


    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

        String token = accessToken;
        // if header contain "Bearer " key then truncate it"
        if (accessToken.startsWith("Bearer ")) {
            token = (accessToken.split("Bearer "))[1];
        }
        final UserEntity userEntity = userBusinessService.getUser(userUuid,token);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName()).lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmail()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());

        // Return the user details along with response status
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}

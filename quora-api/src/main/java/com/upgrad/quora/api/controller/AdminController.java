package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AdminController {


  @Autowired
  private AdminBusinessService adminBusinessService;

  //controller method for the deleteUser endpoint /request
  //receives the userId of the user whose details are to be deleted along with the accesstoken of the user trying to delete
  //validates the signin status of the user making the request and also the role of the user trying to delete the user record
  //throws the authenticationfailedexception if userr is not signed in or if the user is not admin
  //throws the usernotfoundexception if the user deatails with the given usrrId is not found in the db
  //deletes the user record from the users table if no exceptions are thrown
  //returns the id of the user deleted and also appropriate message along with the http status
  //returns the response entity obj after creating the userdeleteresponse with the above info


  @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDeleteResponse> deleteUser(
      @PathVariable("userId") final String userUuid,
      @RequestHeader("authorization") final String accessToken)
      throws UserNotFoundException, AuthorizationFailedException {
    String token = accessToken;
    // if header contain "Bearer " key then truncate it"
    if (accessToken.startsWith("Bearer ")) {
      token = (accessToken.split("Bearer "))[1];
    }
    final UserEntity userEntity = adminBusinessService.deleteUser(userUuid, token);
    UserDeleteResponse userDetailsResponse = new UserDeleteResponse().id(userEntity.getUuid())
        .status("USER SUCCESSFULLY DELETED");
    return new ResponseEntity<UserDeleteResponse>(userDetailsResponse, HttpStatus.OK);
  }
}

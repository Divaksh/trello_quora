package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
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


    @RequestMapping(method = RequestMethod.DELETE, path="admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@RequestHeader("authorization")  final String authorization, @PathVariable("userId") final String userId) throws AuthenticationFailedException, UserNotFoundException {
        String accessToken=authorization.split("Bearer ")[1];
        String requestDetailsString = "delete-user-request@ID " + userId;
        UserAuthTokenEntity userSignedIn =  adminBusinessService.authenticateAdminRequest(accessToken,requestDetailsString);
        UserEntity deletedUser =adminBusinessService.deleteUser(userId);
        UserDeleteResponse userDeleteResponse= new UserDeleteResponse().id(deletedUser.getUuid()).status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}

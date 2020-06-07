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

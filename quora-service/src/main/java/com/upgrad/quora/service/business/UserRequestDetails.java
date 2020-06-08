package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;

//a class created to help process various requests from the user in the application
//userAuthTokenEntity helps to track the status of the user-logged in or not
//request stores the nature of the request
//searchById contains the id to search for a record in the db
//userNotSignedIn and userSignedOut are booleans set as per the status of the user


public class UserRequestDetails {
    private UserAuthTokenEntity userAuthTokenEntity;
    private String request;
    private String searchById;
    private boolean userNotSignedIn;
    private boolean userSignedOut;

    //default constructor
    public UserRequestDetails(){
        this.userAuthTokenEntity=null;
        this.request=null;
        this.searchById=null;
        this.userNotSignedIn=true;
        this.userSignedOut=true;
    }
    //customized constructor
    public UserRequestDetails(UserAuthTokenEntity userAuthTokenEntity,String request,String searchById,boolean userNotSignedIn,boolean userSignedOut){
        this.userAuthTokenEntity=userAuthTokenEntity;
        this.request=request;
        this.searchById=searchById;
        this.userNotSignedIn=userNotSignedIn;
        this.userSignedOut=userSignedOut;
    }

    //getters and setters for all the attributes of the class

    public UserAuthTokenEntity getUserAuthTokenEntity() {
        return userAuthTokenEntity;
    }

    public void setUserAuthTokenEntity(UserAuthTokenEntity userAuthTokenEntity) {
        this.userAuthTokenEntity = userAuthTokenEntity;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSearchById() {
        return searchById;
    }

    public void setSearchById(String searchById) {
        this.searchById = searchById;
    }

    public boolean isUserNotSignedIn() {
        return userNotSignedIn;
    }

    public void setUserNotSignedIn(boolean userNotSignedIn) {
        this.userNotSignedIn = userNotSignedIn;
    }

    public boolean isUserSignedOut() {
        return userSignedOut;
    }

    public void setUserSignedOut(boolean userSignedOut) {
        this.userSignedOut = userSignedOut;
    }
}

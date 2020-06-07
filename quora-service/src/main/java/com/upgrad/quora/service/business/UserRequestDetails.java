package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;

public class UserRequestDetails {
    private UserAuthTokenEntity userAuthTokenEntity;
    private String request;
    private String searchById;
    private boolean userNotSignedIn;
    private boolean userSignedOut;

    public UserRequestDetails(UserAuthTokenEntity userAuthTokenEntity,String request,String searchById,boolean userNotSignedIn,boolean userSignedOut){
        this.userAuthTokenEntity=userAuthTokenEntity;
        this.request=request;
        this.searchById=searchById;
        this.userNotSignedIn=userNotSignedIn;
        this.userSignedOut=userSignedOut;
    }

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

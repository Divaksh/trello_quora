package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

//global exception handler
@ControllerAdvice
public class RestExceptionHandler {


  //handles the AuthenticationFailedException
  @ExceptionHandler(AuthorizationFailedException.class)
  public ResponseEntity<ErrorResponse> authorizationFailedException(
      AuthorizationFailedException exe, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
    );
  }

  //handles the SignUpRestrictedException
  //is invoked when the handled exception is thrown in the application
  //returns the response entity with appropriate error response
  //also includes the http status code in the response

  @ExceptionHandler(SignUpRestrictedException.class)
  public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exc,
      WebRequest request) {
    if (exc.getErrorMessage().equalsIgnoreCase("User is not signed in")) {
      return new ResponseEntity<ErrorResponse>(
          new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
          HttpStatus.UNAUTHORIZED);
    } else {
      return new ResponseEntity<ErrorResponse>(
          new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
          HttpStatus.CONFLICT);

    }

  }

  //handles the UserNotFoundExceptionException
  //is invoked when the handled exception is thrown in the application
  //returns the response entity with appropriate error response
  //also includes the http status code in the response

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exc,
      WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND
    );
  }

  //handles the InvalidQuestionException
  //is invoked when the handled exception is thrown in the application
  //returns the response entity with appropriate error response
  //also includes the http status code in the response

  @ExceptionHandler(InvalidQuestionException.class)
  public ResponseEntity<ErrorResponse> invalidQuestionException(InvalidQuestionException exc,
      WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND
    );
  }

  //handles the AnswerNotFoundException
  //is invoked when the handled exception is thrown in the application
  //returns the response entity with appropriate error response
  //also includes the http status code in the response

  @ExceptionHandler(AnswerNotFoundException.class)
  public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException exc,
      WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND
    );
  }

  //handles the SignOutRestrictedException
  @ExceptionHandler(SignOutRestrictedException.class)
  public ResponseEntity<ErrorResponse> signoutRestrictedException(SignOutRestrictedException exe,
      WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()),
        HttpStatus.UNAUTHORIZED
    );
  }
}

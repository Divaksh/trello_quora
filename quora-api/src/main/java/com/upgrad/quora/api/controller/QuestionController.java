package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController annotation specifies that this class represents a REST API(equivalent of
 * @Controller + @ResponseBody) This Controller class help to perform question operations
 */
@RestController
@RequestMapping
public class QuestionController {

  @Autowired
  private QuestionBusinessService questionService;

  /**
   * This endpoint is used to create a question in the Quora Application which will be shown to all
   * the users. Any user can access this endpoint.
   *
   * @param questionRequest
   * @param accessToken
   * @return return status
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException {

    String token = getAccessToken(accessToken);
    // Create question entity
    final QuestionEntity questionEntity = new QuestionEntity();
    questionEntity.setContent(questionRequest.getContent());
    questionEntity.setUuid(UUID.randomUUID().toString());
    questionEntity.setDate(ZonedDateTime.now());

    // Return response with created question entity
    final QuestionEntity createdQuestionEntity = questionService
        .createQuestion(questionEntity, token);
    QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
        .status("QUESTION CREATED");
    return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
  }

  /**
   * This question controller method handles the get all questions request or the question/all
   * endpoint. This endpoint can be accessed by any user who is authenticated by the quora
   * application
   * <p>
   * Receives @param authorization-bearer authorization which contains the accesstoken in the
   * request header
   *
   * @return ResponseEntity with list of QuestionDetailsResponse
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException {
    String token = getAccessToken(accessToken);
    // Get all questions
    List<QuestionEntity> allQuestions = questionService.getAllQuestions(token);

    // Create response
    List<QuestionDetailsResponse> allQuestionDetailsResponses = new ArrayList<>();

    allQuestions.forEach(questionEntity -> {
      QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
          .content(questionEntity.getContent())
          .id(questionEntity.getUuid());
      allQuestionDetailsResponses.add(questionDetailsResponse);
    });
    // Return response
    return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponses,
        HttpStatus.OK);
  }

  /**
   * This question controller method handles the edit questions request or the
   * question/edit/questionId endpoint. This endpoint can be accessed by any user who is
   * authenticated by the quora application Only the owner and the admin can edit the question
   * Receives the Question edit request object
   *
   * @param questionEditRequest
   * @param questionId
   * @param accessToken
   * @return ResponseEntity with QuestionEditResponse.
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestion(
      final QuestionEditRequest questionEditRequest,
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    String token = getAccessToken(accessToken);
    QuestionEntity questionEntity = new QuestionEntity();
    questionEntity.setContent(questionEditRequest.getContent());
    questionEntity.setUuid(questionId);

    QuestionEntity updatedQuestionEntity = questionService.editQuestion(questionEntity, token);
    QuestionEditResponse questionEditResponse = new QuestionEditResponse()
        .id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");
    return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
  }

  /**
   * This question controller method handles the delete a questions request or the
   * question/delete/questionId endpoint. This endpoint can be accessed by any user who is
   * authenticated by the quora application But only the owner of the questionand the admin can
   * delete the question
   *
   * @param questionId  Question Id to delete from Server
   * @param accessToken this variable helps to authenticate the user
   * @return ResponseEntity with QuestionDeleteResponse
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    String token = getAccessToken(accessToken);
    questionService.deleteQuestion(questionId, token);

    QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId)
        .status("QUESTION DELETED");
    return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
  }

  /**
   * This question controller method handles the get all questions by a user request or the
   * question/all/questionId endpoint. This endpoint can be accessed by any user who is
   * authenticated by the quora application
   *
   * @param userId
   * @param accessToken
   * @return ResponseEntity with list of QuestionDetailsResponse
   */
  @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
      @PathVariable("userId") final String userId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {
    String token = getAccessToken(accessToken);
    // Get all questions for requested user
    List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId, accessToken);

    // Create response
    List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

    allQuestions.forEach(questionEntity -> {
      QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
          .content(questionEntity.getContent())
          .id(questionEntity.getUuid());
      allQuestionDetailsResponse.add(questionDetailsResponse);
    });

    // Return response
    return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse,
        HttpStatus.OK);
  }

  /**
   * User can give only Access token or Bearer accessToken as input.
   *
   * @param accessToken
   * @return token
   */
  private String getAccessToken(String accessToken) {
    if (accessToken.startsWith("Bearer ")) {
      return (accessToken.split("Bearer "))[1];
    }
    return accessToken;
  }

}

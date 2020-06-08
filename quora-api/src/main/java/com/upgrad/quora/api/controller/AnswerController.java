package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AnswerController {

  @Autowired
  AnswerBusinessService answerBusinessService;

  /**
   * This answer controller method handles the create a answer request or the
   * question/questionId/answer/create endpoint. This endpoint can be accessed by any user who is
   * authenticated by the quora application
   *
   * @param answerRequest
   * @param questionId
   * @param accessToken
   * @return ResponseEntity
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    String token = getAccessToken(accessToken);

    final AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAnswer(answerRequest.getAnswer());

    final AnswerEntity createdAnswerEntity =
        answerBusinessService.createAnswer(answerEntity, questionId, token);
    AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid())
        .status("ANSWER CREATED");
    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
  }

  /**
   * This answer controller method handles the edit answer request or the answer/edit/answerId
   * endpoint. This endpoint can be accessed by any user who is authenticated by the quora
   * application Only the owner of the answer can edit the answer Receives the answer edit request
   * object
   *
   * @param answerEditRequest
   * @param answerId
   * @param accessToken
   * @return ResponseEntity
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswerContent(
      final AnswerEditRequest answerEditRequest,
      @PathVariable("answerId") final String answerId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {

    String token = getAccessToken(accessToken);
    // Created answer entity for further update
    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAnswer(answerEditRequest.getContent());
    answerEntity.setUuid(answerId);

    // Return response with updated answer entity
    AnswerEntity updatedAnswerEntity = answerBusinessService.editAnswerContent(answerEntity, token);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse()
        .id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
    return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
  }

  /**
   * This answer controller method handles the delete a answer request or the answer/delete/answerId
   * endpoint. This endpoint can be accessed by any user who is authenticated by the quora
   * application But only the owner of the answer and  admin can delete the answer
   *
   * @param answerId
   * @param accessToken
   * @return ResponseEntity
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
      @PathVariable("answerId") final String answerId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {
    String token = getAccessToken(accessToken);

    answerBusinessService.deleteAnswer(answerId, token);
    AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId)
        .status("ANSWER DELETED");
    return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
  }

  /**
   * This answer controller method handles the get all answers to a question request or the
   * answer/all/questionId endpoint. This endpoint can be accessed by any user who is authenticated
   * by the quora application
   *
   * @param questionId
   * @param accessToken
   * @return RequestMapping
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    String token = getAccessToken(accessToken);
    List<AnswerEntity> allAnswers = answerBusinessService
        .getAllAnswersToQuestion(questionId, token);
    List<AnswerDetailsResponse> allAnswersResponse = new ArrayList<AnswerDetailsResponse>();

    allAnswers.forEach(answers -> {
      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
          .answerContent(answers.getAnswer())
          .questionContent(answers.getQuestionEntity().getContent())
          .id(answers.getUuid());
      allAnswersResponse.add(answerDetailsResponse);
    });

    return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswersResponse, HttpStatus.OK);
  }

  /**
   * User can give only Access token or Bearer <accesstoken> as input.
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

package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    /**
     * This answer controller method handles the create a answer request or the question/questionId/answer/create endpoint.
     * This endpoint can be accessed by any user who is authenticated by the quora application
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
                                                       @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        String token = getAccessToken(accessToken);

        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());

        final AnswerEntity createdAnswerEntity =
                answerBusinessService.createAnswer(answerEntity, questionId, token);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
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

package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
 * This Controller class help to perform question operations
 */
@RestController
@RequestMapping
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionService;

    /**
     * This endpoint is used to create a question in the Quora Application which will be shown to all the users. Any user can access this endpoint.
     *
     * @param questionRequest
     * @param accessToken
     * @return return status
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        String token = getAccessToken(accessToken);
        // Create question entity
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());

        // Return response with created question entity
        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity, token);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     *  This question controller method handles the get all questions request or the question/all endpoint.
     *  This endpoint can be accessed by any user who is authenticated by the quora application
     *
     * Receives @param authorization-bearer authorization which contains the accesstoken in the request header
     * @return ResponseEntity with list of QuestionDetailsResponse
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
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
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponses, HttpStatus.OK);
    }

    private String getAccessToken(String accessToken) {
        // if header contain "Bearer " key then truncate it"
        if (accessToken.startsWith("Bearer ")) {
            return (accessToken.split("Bearer "))[1];
        }
        return accessToken;
    }

}

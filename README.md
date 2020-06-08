# IIIT-B Case Study - Create Quora like Web-App using RESTful Web APIs.

* JSON files containing all details related to each endpoint were provided.
* After observation of all HTTP methods to make calls, URL pattern, input/output parameters and status code implementation is done.
* All the exceptions have been implemented as per the requirement.
* Since the database is not mocked, "quora_test.sql" file is given to create records in the database to pass all the test cases.
* All the test cases would only pass database has these records in the database. 
* Always refer to the resource's uuid whenever id of the resource is mentioned.

# Endpoints
## UserController 
* signup - "/user/signup"
* signin - "/user/signin"
* signout - "/user/signout"

## CommonController
* userProfile - "/userprofile/{userId}"

## AdminController
* userDelete - "/admin/user/{userId}"

## QuestionController
* createQuestion - "/question/create"
* getAllQuestions - "/question/all"
* editQuestionContent - "/question/edit/{questionId}"
* deleteQuestion - "/question/delete/{questionId}"
* getAllQuestionsByUser - "question/all/{userId}"

## AnswerController
* createAnswer - "/question/{questionId}/answer/create"
* editAnswerContent - "/answer/edit/{answerId}"
* deleteAnswer - "/answer/delete/{answerId}"
* getAllAnswersToQuestion - "answer/all/{questionId}"

# Requirements
* A Good Working Brain

* IDE

* Postgres (pgAdmin4)

* JDK (1.8 or newer) from Java SE development

* Maven

* Environmental variables for JDK, and Maven

* Download PostgreSQL JDBC 4.2 driver and add it as external jar file to the project


# Building & running the project

* Import the project into IDE
* mvn clean install -DskipTests
* mvn clean install -Psetup
package com.huskie.dwarves.surveyQuestion.exceptions

class SurveyQuestionNotFoundException(id : Long) :
        RuntimeException("Survey Question with id $id not found")

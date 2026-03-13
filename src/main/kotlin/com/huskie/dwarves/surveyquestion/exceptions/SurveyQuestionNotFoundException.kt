package com.huskie.dwarves.surveyquestion.exceptions

class SurveyQuestionNotFoundException(id : Long) :
        RuntimeException("Survey Question with id $id not found")

package com.huskie.dwarves.survey.exceptions

class SurveyNotFoundException(id:Long) :
    RuntimeException("Survey with id $id not found")
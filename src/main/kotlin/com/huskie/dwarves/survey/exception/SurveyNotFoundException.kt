package com.huskie.dwarves.survey.exception

class SurveyNotFoundException(id:Long) :
    RuntimeException("Survey with id $id not found")
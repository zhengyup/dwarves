package com.huskie.dwarves.surveyquestion.exceptions

class DuplicateSurveyQuestionDisplayOrderException(id : Long, displayOrder : Int) : RuntimeException
("Duplicate Survey " +
        "Display order $displayOrder already exists for survey question $id")
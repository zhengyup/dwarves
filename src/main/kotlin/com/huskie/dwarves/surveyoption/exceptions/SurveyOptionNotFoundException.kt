package com.huskie.dwarves.surveyoption.exceptions

class SurveyOptionNotFoundException(id: Long) : RuntimeException("Survey option with id $id not found")
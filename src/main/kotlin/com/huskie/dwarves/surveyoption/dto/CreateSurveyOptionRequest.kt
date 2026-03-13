package com.huskie.dwarves.surveyoption.dto

data class CreateSurveyOptionRequest(
        val surveyQuestionId: Long,
        val optionText: String,
        val optionValue: String? = null,
        val displayOrder: Int
)
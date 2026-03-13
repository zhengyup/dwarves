package com.huskie.dwarves.surveyoption.dto
data class UpdateSurveyOptionRequest(
        val optionText: String,
        val optionValue: String? = null,
        val displayOrder: Int
)
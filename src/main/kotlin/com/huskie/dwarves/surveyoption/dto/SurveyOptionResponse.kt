package com.huskie.dwarves.surveyoption.dto

import java.time.LocalDateTime

data class SurveyOptionResponse(
        val id: Long,
        val surveyQuestionId: Long,
        val optionText: String,
        val optionValue: String?,
        val displayOrder: Int,
        val createdAt: LocalDateTime
)
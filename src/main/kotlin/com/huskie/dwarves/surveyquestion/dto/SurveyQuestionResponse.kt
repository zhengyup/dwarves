package com.huskie.dwarves.surveyquestion.dto

class SurveyQuestionResponse (
        val id: Long? = null,
        val surveyId: Long,
        val questionText: String,
        val questionType: String,
        val isRequired: Boolean,
        val displayOrder: Int,
)
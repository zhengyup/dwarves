package com.huskie.dwarves.surveyQuestion.dto

import com.huskie.dwarves.survey.entity.Survey
import jakarta.persistence.*
import java.time.LocalDateTime

class SurveyQuestionResponse (
        val id: Long? = null,
        val surveyId: Long,
        val questionText: String,
        val questionType: String,
        val isRequired: Boolean,
        val displayOrder: Int,
)
package com.huskie.dwarves.surveyquestion.dto

import jakarta.validation.constraints.NotBlank

class UpdateSurveyQuestionRequest (
        @field:NotBlank
        val surveyId : Long,

        @field:NotBlank
        val questionText: String,

        @field:NotBlank
        val questionType: String,

        @field:NotBlank
        val isRequired: Boolean,

        @field:NotBlank
        val displayOrder: Int,
)



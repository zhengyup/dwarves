package com.huskie.dwarves.survey.dto

import jakarta.validation.constraints.NotBlank

class UpdateSurveyRequest (
        @field:NotBlank
        val name: String,

        @field:NotBlank
        val organizationId : Long,

        val description : String? = null,
)

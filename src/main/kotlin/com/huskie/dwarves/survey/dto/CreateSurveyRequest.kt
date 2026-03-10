package com.huskie.dwarves.survey.dto

import jakarta.validation.constraints.NotBlank

class CreateSurveyRequest (
        @field:NotBlank
        val name: String,

        @field:NotBlank
        val organizationId : Long,

        val description : String? = null,
)



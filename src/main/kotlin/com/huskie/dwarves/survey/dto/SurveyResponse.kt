package com.huskie.dwarves.survey.dto

import java.time.LocalDateTime

data class SurveyResponse(
        val id: Long,
        val organizationId: Long,
        val name: String,
        val description : String? = null,
)
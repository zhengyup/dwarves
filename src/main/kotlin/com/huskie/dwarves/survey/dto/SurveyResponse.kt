package com.huskie.dwarves.survey.dto

import com.huskie.dwarves.organization.entity.Organization
import java.time.LocalDateTime

data class SurveyResponse(
        val id: Long,
        val organizationId: Long,
        val name: String,
        val description : String? = null,
        val createdAt: LocalDateTime
)
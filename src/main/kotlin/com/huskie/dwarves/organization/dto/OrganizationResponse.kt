package com.huskie.dwarves.organization.dto

import java.time.LocalDateTime

data class OrganizationResponse(
        val id: Long,
        val name: String,
        val code: String,
        val createdAt: LocalDateTime
)

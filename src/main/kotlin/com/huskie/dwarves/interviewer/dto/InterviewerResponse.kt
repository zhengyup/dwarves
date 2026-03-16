package com.huskie.dwarves.interviewer.dto

import java.time.LocalDateTime

data class InterviewerResponse(
        val id: Long,
        val name: String,
        val email: String?,
        val createdAt: LocalDateTime
)
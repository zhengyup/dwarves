package com.huskie.dwarves.submission.dto

import java.time.LocalDateTime

data class SubmissionResponse(
        val id: Long,
        val interviewerId: Long,
        val surveyId: Long,
        val createdAt: LocalDateTime
)
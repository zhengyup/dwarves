package com.huskie.dwarves.submission.dto

data class CreateSubmissionRequest(
        val interviewerId: Long,
        val surveyId: Long
)
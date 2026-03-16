package com.huskie.dwarves.submission.dto

data class UpdateSubmissionRequest(
        val interviewerId: Long,
        val surveyId: Long
)
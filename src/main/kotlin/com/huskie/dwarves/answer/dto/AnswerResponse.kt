package com.huskie.dwarves.answer.dto

import java.time.LocalDateTime

data class AnswerResponse(
        val id: Long,
        val submissionId: Long,
        val surveyQuestionId: Long,
        val surveyOptionId: Long?,
        val answerText: String?,
        val createdAt: LocalDateTime
)
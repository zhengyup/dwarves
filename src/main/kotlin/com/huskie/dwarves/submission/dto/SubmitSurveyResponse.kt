package com.huskie.dwarves.submission.dto

import com.huskie.dwarves.answer.dto.AnswerResponse
import java.time.LocalDateTime

data class SubmitSurveyResponse(
        val id: Long,
        val interviewerId: Long,
        val surveyId: Long,
        val answerResponses : List<AnswerResponse>,
        val submittedAt : LocalDateTime
)

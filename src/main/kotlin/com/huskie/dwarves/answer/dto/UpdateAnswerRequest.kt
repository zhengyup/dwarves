package com.huskie.dwarves.answer.dto

data class UpdateAnswerRequest(
        val submissionId: Long,
        val surveyQuestionId: Long,
        val surveyOptionId: Long? = null,
        val answerText: String? = null
)
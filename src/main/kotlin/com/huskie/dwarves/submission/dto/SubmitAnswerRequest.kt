package com.huskie.dwarves.submission.dto

data class SubmitAnswerRequest(
        val questionId: Long,
        val answerText: String? = null,
        val selectedOptionId: Long? = null
)
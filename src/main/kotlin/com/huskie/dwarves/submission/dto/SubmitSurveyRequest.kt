package com.huskie.dwarves.submission.dto

//to create a Submission by submitting a Survey
data class SubmitSurveyRequest (
        val interviewerId : Long,
        val surveyId : Long,
        val answers : List<SubmitAnswerRequest>
)
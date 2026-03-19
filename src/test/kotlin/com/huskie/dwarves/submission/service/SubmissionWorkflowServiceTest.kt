package com.huskie.dwarves.submission.service

import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.submission.dto.SubmitAnswerRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyRequest
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Optional
import com.huskie.dwarves.util.*
class SubmissionWorkflowServiceTest (
        private val surveyRepository: SurveyRepository = mock(),
        private val questionRepository: SurveyQuestionRepository = mock(),
        private val submissionWorkflowService : SubmissionWorkflowService = SubmissionWorkflowService(surveyRepository,
                questionRepository)
) {
    @Test
    fun `submit survey should return saved submission and answers`() {
        val answers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L, answerText = "Test answer 1",
                ),
                SubmitAnswerRequest(
                        questionId = 2L, booleanValue = true,
                ),
                SubmitAnswerRequest(
                        questionId = 3L, selectedOptionId = 1L
                )
        )
        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = answers
        )

        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of())


        val result = submissionWorkflowService.submitSurvey(request)
    }
}

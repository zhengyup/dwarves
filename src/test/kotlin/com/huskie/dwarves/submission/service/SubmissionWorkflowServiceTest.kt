package com.huskie.dwarves.submission.service

import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.submission.dto.SubmitAnswerRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyRequest
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Optional
import com.huskie.dwarves.util.*
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class SubmissionWorkflowServiceTest {

    private val answerRepository: AnswerRepository = mock()
    private val interviewerRepository: InterviewerRepository = mock()
    private val questionRepository: SurveyQuestionRepository = mock()
    private val surveyOptionRepository: SurveyOptionRepository = mock()
    private val surveyRepository: SurveyRepository = mock()
    private val submissionRepository: SubmissionRepository = mock()
    private val submissionWorkflowService : SubmissionWorkflowService = SubmissionWorkflowService(answerRepository, interviewerRepository, questionRepository, surveyOptionRepository, surveyRepository, submissionRepository)

    @Test
    fun `submit survey should return saved submission and answers`() {
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L, answerText = "Test answer 1",
                ),
                SubmitAnswerRequest(
                        questionId = 2L, selectedOptionId = 1L
                ),
        )

        val survey = makeSurvey(1L)
        val interviewer = makeInterviewer(1L)
        val option = makeSurveyOption(1L)
        val submission = makeSubmission(id = 1L, interviewer= interviewer, survey = survey)

        val questions = listOf<SurveyQuestion>(
                SurveyQuestion(
                        id = 1L,
                        survey = survey,
                        questionText = "Open ended question 1",
                        isRequired = true,
                        questionType = "TEXT",
                        displayOrder = 1,
                ),
                SurveyQuestion(
                        id = 2L,
                        survey = survey,
                        questionText = "MCQ 1",
                        isRequired = true,
                        questionType = "MCQ",
                        displayOrder = 2,
                )
        )

        val savedAnswers = listOf<Answer>(
                Answer(
                        id = 1L,
                        submission = submission,
                        surveyQuestion = questions[0],
                        answerText = "Test answer 1",
                ),
                Answer(
                        id = 2L,
                        submission = submission,
                        surveyQuestion = questions[1],
                        surveyOption = option
                )
        )

        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )



        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(questions)
        whenever(questionRepository.findById(1L)).thenReturn(Optional.of(questions[0]))
        whenever(questionRepository.findById(2L)).thenReturn(Optional.of(questions[1]))
        whenever(surveyOptionRepository.findById(1L)).thenReturn(Optional.of(option))
        whenever(submissionRepository.save(any<Submission>())).thenReturn(submission)
        whenever(answerRepository.saveAll(any<List<Answer>>())).thenReturn(savedAnswers)
        val result = submissionWorkflowService.submitSurvey(request)
        assertEquals(result.submissionId, submission.id)
        assertEquals(result.interviewerId, interviewer.id)
        assertEquals(result.surveyId, survey.id)
        for ((index, answerResponse) in result.answerResponses.withIndex()) {
            assertEquals(answerResponse.id, savedAnswers[index].id)
            assertEquals(answerResponse.submissionId, savedAnswers[index].submission.id)
            assertEquals(answerResponse.surveyQuestionId, savedAnswers[index].surveyQuestion.id)
            if (answerResponse.surveyOptionId != null) {
                val savedOption = requireNotNull(savedAnswers[index].surveyOption)
                val savedOptionId = requireNotNull(savedOption.id)
                assertEquals(answerResponse.surveyOptionId, savedOptionId)
            }
            assertEquals(answerResponse.answerText, savedAnswers[index].answerText)
        }
        verify(submissionRepository).save(any<Submission>())
        verify(answerRepository).saveAll(any<List<Answer>>())
        verify(surveyRepository).findById(1L)
        verify(questionRepository).findById(1L)
        verify(questionRepository).findById(2L)
        verify(surveyOptionRepository).findById(1L)
    }
}




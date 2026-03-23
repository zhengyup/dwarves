package com.huskie.dwarves.submission.service

import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.submission.dto.SubmitAnswerRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyRequest
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.submission.exceptions.IncorrectAnswerTypeException
import com.huskie.dwarves.submission.exceptions.MissingRequiredAnswerException
import com.huskie.dwarves.submission.exceptions.OverlappingAnswerException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Optional
import com.huskie.dwarves.util.*
import org.junit.jupiter.api.Assertions.assertThrows

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.lang.IllegalArgumentException
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

        val request = makeSubmitSurveyRequest()

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
    }

    @Test
    fun `submit survey throws error when survey does not exist`() {
        val request = makeSubmitSurveyRequest()
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows(SurveyNotFoundException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit survey throws error when interviewer does not exist`() {
        val request = makeSubmitSurveyRequest()
        val survey = makeSurvey()
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows(InterviewerNotFoundException::class.java){
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit survey throws error when duplicate question id in answers provided`() {
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L, answerText = "Test answer 1",
                ),
                SubmitAnswerRequest(
                        questionId = 1L, selectedOptionId = 1L
                ),
        )
        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        assertThrows(OverlappingAnswerException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit survey throws error when required question is not answered`() {
        val request = makeSubmitSurveyRequest()
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
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
                        isRequired = false,
                        questionType = "MCQ",
                        displayOrder = 2,
                ),
                SurveyQuestion(
                        id = 3L,
                        survey = survey,
                        questionText = "MCQ 1",
                        isRequired = true,
                        questionType = "MCQ",
                        displayOrder = 2,
                )
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(
        questions)
        assertThrows(MissingRequiredAnswerException(3L) :: class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit request should throw an error when text question is not answered with text`() {
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L,
                ),
        )
        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )

        val questions = listOf<SurveyQuestion>(
                SurveyQuestion(
                        id = 1L,
                        survey = survey,
                        questionText = "Open ended question 1",
                        isRequired = true,
                        questionType = "TEXT",
                        displayOrder = 1,
                ),
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(
                questions)
        assertThrows(IncorrectAnswerTypeException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit request should throw an error when an MCQ question is not answered with an optionId`() {
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L,
                ),
        )
        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )

        val questions = listOf<SurveyQuestion>(
                SurveyQuestion(
                        id = 1L,
                        survey = survey,
                        questionText = "MCQ question 1",
                        isRequired = true,
                        questionType = "MCQ",
                        displayOrder = 1,
                ),
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(
                questions)
        assertThrows(IncorrectAnswerTypeException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit survey throws an error when answer provides option that does not exist`() {
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L,
                        selectedOptionId = 1L
                ),
        )
        val request = SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )

        val questions = listOf<SurveyQuestion>(
                SurveyQuestion(
                        id = 1L,
                        survey = survey,
                        questionText = "MCQ question 1",
                        isRequired = true,
                        questionType = "MCQ",
                        displayOrder = 1,
                ),
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(
                questions)
        whenever(surveyOptionRepository.existsById(1L)).thenReturn(false)
        assertThrows(SurveyOptionNotFoundException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    @Test
    fun `submit survey throws error when answer does not correspond to any question`() {
        val survey = makeSurvey()
        val interviewer = makeInterviewer()
        val request = makeSubmitSurveyRequest()
        val questions = listOf<SurveyQuestion>(
                SurveyQuestion(
                        id = 1L,
                        survey = survey,
                        questionText = "Text question 1",
                        isRequired = true,
                        questionType = "TEXT",
                        displayOrder = 1,
                ),
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(survey))
        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(questionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(
                questions)
        assertThrows(SurveyQuestionNotFoundException::class.java) {
            submissionWorkflowService.submitSurvey(request)
        }
    }

    private fun makeSubmitSurveyRequest() : SubmitSurveyRequest  {
        val submittedAnswers = listOf<SubmitAnswerRequest>(
                SubmitAnswerRequest(
                        questionId = 1L, answerText = "Test answer 1",
                ),
                SubmitAnswerRequest(
                        questionId = 2L, selectedOptionId = 1L
                ),
        )
        return SubmitSurveyRequest(
                interviewerId = 1L,
                surveyId = 1L,
                answers = submittedAnswers
        )
    }


}




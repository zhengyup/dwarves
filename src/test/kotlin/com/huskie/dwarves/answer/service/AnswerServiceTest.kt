package com.huskie.dwarves.answer.service

import com.huskie.dwarves.answer.dto.CreateAnswerRequest
import com.huskie.dwarves.answer.dto.UpdateAnswerRequest
import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.answer.exceptions.AnswerNotFoundException
import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.submission.exception.SubmissionNotFoundException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.util.*
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class AnswerServiceTest {

    private val answerRepository: AnswerRepository = mock()
    private val submissionRepository: SubmissionRepository = mock()
    private val surveyQuestionRepository: SurveyQuestionRepository = mock()
    private val surveyOptionRepository: SurveyOptionRepository = mock()

    private val answerService = AnswerService(
            answerRepository,
            submissionRepository,
            surveyQuestionRepository,
            surveyOptionRepository
    )

    @Test
    fun `create answer should return saved response for text answer`() {
        val request = CreateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 2L,
                surveyOptionId = null,
                answerText = "Yes I know what diabetes is"
        )

        val submission = makeSubmission(id = 1L)
        val surveyQuestion = makeSurveyQuestion(id = 2L)
        val savedAnswer = makeAnswer(
                id = 10L,
                submission = submission,
                surveyQuestion = surveyQuestion,
                surveyOption = null,
                answerText = "Yes I know what diabetes is"
        )

        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(2L)).thenReturn(Optional.of(surveyQuestion))
        whenever(answerRepository.save(any<Answer>())).thenReturn(savedAnswer)

        val result = answerService.createAnswer(request)

        assertEquals(10L, result.id)
        assertEquals(1L, result.submissionId)
        assertEquals(2L, result.surveyQuestionId)
        assertEquals(null, result.surveyOptionId)
        assertEquals("Yes I know what diabetes is", result.answerText)

        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(2L)
        verify(answerRepository).save(any<Answer>())
    }

    @Test
    fun `create answer should return saved response for option answer`() {
        val request = CreateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 2L,
                surveyOptionId = 3L,
                answerText = null
        )

        val submission = makeSubmission(id = 1L)
        val surveyQuestion = makeSurveyQuestion(id = 2L)
        val surveyOption = makeSurveyOption(id = 3L, surveyQuestion = surveyQuestion)
        val savedAnswer = makeAnswer(
                id = 10L,
                submission = submission,
                surveyQuestion = surveyQuestion,
                surveyOption = surveyOption,
                answerText = null
        )

        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(2L)).thenReturn(Optional.of(surveyQuestion))
        whenever(surveyOptionRepository.findById(3L)).thenReturn(Optional.of(surveyOption))
        whenever(answerRepository.save(any<Answer>())).thenReturn(savedAnswer)

        val result = answerService.createAnswer(request)

        assertEquals(10L, result.id)
        assertEquals(1L, result.submissionId)
        assertEquals(2L, result.surveyQuestionId)
        assertEquals(3L, result.surveyOptionId)
        assertEquals(null, result.answerText)

        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(2L)
        verify(surveyOptionRepository).findById(3L)
        verify(answerRepository).save(any<Answer>())
    }

    @Test
    fun `create answer should throw when submission not found`() {
        val request = CreateAnswerRequest(
                submissionId = 99L,
                surveyQuestionId = 2L,
                surveyOptionId = null,
                answerText = "text"
        )

        whenever(submissionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubmissionNotFoundException::class.java) {
            answerService.createAnswer(request)
        }

        verify(submissionRepository).findById(99L)
        verify(surveyQuestionRepository, never()).findById(any())
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `create answer should throw when survey question not found`() {
        val request = CreateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 99L,
                surveyOptionId = null,
                answerText = "text"
        )

        val submission = makeSubmission(id = 1L)

        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyQuestionNotFoundException::class.java) {
            answerService.createAnswer(request)
        }

        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(99L)
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `create answer should throw when survey option not found`() {
        val request = CreateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 2L,
                surveyOptionId = 99L,
                answerText = null
        )

        val submission = makeSubmission(id = 1L)
        val surveyQuestion = makeSurveyQuestion(id = 2L)

        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(2L)).thenReturn(Optional.of(surveyQuestion))
        whenever(surveyOptionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyOptionNotFoundException::class.java) {
            answerService.createAnswer(request)
        }

        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(2L)
        verify(surveyOptionRepository).findById(99L)
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `get answer by id should return response`() {
        val answer = makeAnswer(
                id = 10L,
                submission = makeSubmission(id = 1L),
                surveyQuestion = makeSurveyQuestion(id = 2L),
                surveyOption = makeSurveyOption(id = 3L),
                answerText = "sample text"
        )

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(answer))

        val result = answerService.getAnswerById(10L)

        assertEquals(10L, result.id)
        assertEquals(1L, result.submissionId)
        assertEquals(2L, result.surveyQuestionId)
        assertEquals(3L, result.surveyOptionId)
        assertEquals("sample text", result.answerText)

        verify(answerRepository).findById(10L)
    }

    @Test
    fun `get answer by id should throw when answer not found`() {
        whenever(answerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(AnswerNotFoundException::class.java) {
            answerService.getAnswerById(99L)
        }

        verify(answerRepository).findById(99L)
    }

    @Test
    fun `get all answers should return all responses`() {
        val answer1 = makeAnswer(
                id = 10L,
                submission = makeSubmission(id = 1L),
                surveyQuestion = makeSurveyQuestion(id = 2L),
                answerText = "text 1"
        )
        val answer2 = makeAnswer(
                id = 11L,
                submission = makeSubmission(id = 3L),
                surveyQuestion = makeSurveyQuestion(id = 4L),
                answerText = "text 2"
        )

        whenever(answerRepository.findAll()).thenReturn(listOf(answer1, answer2))

        val result = answerService.getAllAnswers()

        assertEquals(2, result.size)
        assertEquals(10L, result[0].id)
        assertEquals(11L, result[1].id)

        verify(answerRepository).findAll()
    }

    @Test
    fun `get answers by submission id should return matching responses`() {
        val submission = makeSubmission(id = 1L)
        val answer1 = makeAnswer(id = 10L, submission = submission, surveyQuestion = makeSurveyQuestion(id = 2L))
        val answer2 = makeAnswer(id = 11L, submission = submission, surveyQuestion = makeSurveyQuestion(id = 3L))

        whenever(answerRepository.findBySubmissionId(1L)).thenReturn(listOf(answer1, answer2))

        val result = answerService.getAnswersBySubmissionId(1L)

        assertEquals(2, result.size)
        assertEquals(1L, result[0].submissionId)
        assertEquals(1L, result[1].submissionId)

        verify(answerRepository).findBySubmissionId(1L)
    }

    @Test
    fun `get answers by survey question id should return matching responses`() {
        val surveyQuestion = makeSurveyQuestion(id = 2L)
        val answer1 = makeAnswer(id = 10L, submission = makeSubmission(id = 1L), surveyQuestion = surveyQuestion)
        val answer2 = makeAnswer(id = 11L, submission = makeSubmission(id = 2L), surveyQuestion = surveyQuestion)

        whenever(answerRepository.findBySurveyQuestionId(2L)).thenReturn(listOf(answer1, answer2))

        val result = answerService.getAnswersBySurveyQuestionId(2L)

        assertEquals(2, result.size)
        assertEquals(2L, result[0].surveyQuestionId)
        assertEquals(2L, result[1].surveyQuestionId)

        verify(answerRepository).findBySurveyQuestionId(2L)
    }

    @Test
    fun `update answer should return updated response`() {
        val request = UpdateAnswerRequest(
                submissionId = 3L,
                surveyQuestionId = 4L,
                surveyOptionId = 5L,
                answerText = "updated answer"
        )

        val existingAnswer = makeAnswer(
                id = 10L,
                submission = makeSubmission(id = 1L),
                surveyQuestion = makeSurveyQuestion(id = 2L),
                surveyOption = null,
                answerText = "old answer"
        )

        val updatedSubmission = makeSubmission(id = 3L)
        val updatedQuestion = makeSurveyQuestion(id = 4L)
        val updatedOption = makeSurveyOption(id = 5L, surveyQuestion = updatedQuestion)

        val updatedAnswer = existingAnswer.copy(
                submission = updatedSubmission,
                surveyQuestion = updatedQuestion,
                surveyOption = updatedOption,
                answerText = "updated answer"
        )

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(existingAnswer))
        whenever(submissionRepository.findById(3L)).thenReturn(Optional.of(updatedSubmission))
        whenever(surveyQuestionRepository.findById(4L)).thenReturn(Optional.of(updatedQuestion))
        whenever(surveyOptionRepository.findById(5L)).thenReturn(Optional.of(updatedOption))
        whenever(answerRepository.save(any<Answer>())).thenReturn(updatedAnswer)

        val result = answerService.updateAnswer(10L, request)

        assertEquals(10L, result.id)
        assertEquals(3L, result.submissionId)
        assertEquals(4L, result.surveyQuestionId)
        assertEquals(5L, result.surveyOptionId)
        assertEquals("updated answer", result.answerText)

        verify(answerRepository).findById(10L)
        verify(submissionRepository).findById(3L)
        verify(surveyQuestionRepository).findById(4L)
        verify(surveyOptionRepository).findById(5L)
        verify(answerRepository).save(any<Answer>())
    }

    @Test
    fun `update answer should throw when answer not found`() {
        val request = UpdateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 2L,
                surveyOptionId = null,
                answerText = "updated"
        )

        whenever(answerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(AnswerNotFoundException::class.java) {
            answerService.updateAnswer(99L, request)
        }

        verify(answerRepository).findById(99L)
        verify(submissionRepository, never()).findById(any())
        verify(surveyQuestionRepository, never()).findById(any())
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `update answer should throw when submission not found`() {
        val request = UpdateAnswerRequest(
                submissionId = 99L,
                surveyQuestionId = 2L,
                surveyOptionId = null,
                answerText = "updated"
        )

        val existingAnswer = makeAnswer(id = 10L)

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(existingAnswer))
        whenever(submissionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubmissionNotFoundException::class.java) {
            answerService.updateAnswer(10L, request)
        }

        verify(answerRepository).findById(10L)
        verify(submissionRepository).findById(99L)
        verify(surveyQuestionRepository, never()).findById(any())
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `update answer should throw when survey question not found`() {
        val request = UpdateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 99L,
                surveyOptionId = null,
                answerText = "updated"
        )

        val existingAnswer = makeAnswer(id = 10L)
        val submission = makeSubmission(id = 1L)

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(existingAnswer))
        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyQuestionNotFoundException::class.java) {
            answerService.updateAnswer(10L, request)
        }

        verify(answerRepository).findById(10L)
        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(99L)
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `update answer should throw when survey option not found`() {
        val request = UpdateAnswerRequest(
                submissionId = 1L,
                surveyQuestionId = 2L,
                surveyOptionId = 99L,
                answerText = null
        )

        val existingAnswer = makeAnswer(id = 10L)
        val submission = makeSubmission(id = 1L)
        val question = makeSurveyQuestion(id = 2L)

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(existingAnswer))
        whenever(submissionRepository.findById(1L)).thenReturn(Optional.of(submission))
        whenever(surveyQuestionRepository.findById(2L)).thenReturn(Optional.of(question))
        whenever(surveyOptionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyOptionNotFoundException::class.java) {
            answerService.updateAnswer(10L, request)
        }

        verify(answerRepository).findById(10L)
        verify(submissionRepository).findById(1L)
        verify(surveyQuestionRepository).findById(2L)
        verify(surveyOptionRepository).findById(99L)
        verify(answerRepository, never()).save(any<Answer>())
    }

    @Test
    fun `delete answer should delete successfully`() {
        val answer = makeAnswer(id = 10L)

        whenever(answerRepository.findById(10L)).thenReturn(Optional.of(answer))

        answerService.deleteAnswer(10L)

        verify(answerRepository).findById(10L)
        verify(answerRepository).delete(answer)
    }

    @Test
    fun `delete answer should throw when answer not found`() {
        whenever(answerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(AnswerNotFoundException::class.java) {
            answerService.deleteAnswer(99L)
        }

        verify(answerRepository).findById(99L)
        verify(answerRepository, never()).delete(any<Answer>())
    }
}
package com.huskie.dwarves.submission.service

import com.huskie.dwarves.interviewer.entity.Interviewer
import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.submission.dto.CreateSubmissionRequest
import com.huskie.dwarves.submission.dto.UpdateSubmissionRequest
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.submission.exception.SubmissionNotFoundException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

class SubmissionServiceTest {

    private val submissionRepository: SubmissionRepository = mock()
    private val interviewerRepository: InterviewerRepository = mock()
    private val surveyRepository: SurveyRepository = mock()

    private val submissionService = SubmissionService(
            submissionRepository,
            interviewerRepository,
            surveyRepository
    )

    @Test
    fun `create submission should return saved response`() {
        val request = CreateSubmissionRequest(
                interviewerId = 1L,
                surveyId = 2L
        )

        val interviewer = makeInterviewer(id = 1L)
        val survey = makeSurvey(id = 2L)
        val savedSubmission = makeSubmission(
                id = 10L,
                interviewer = interviewer,
                survey = survey
        )

        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(surveyRepository.findById(2L)).thenReturn(Optional.of(survey))
        whenever(submissionRepository.save(any<Submission>())).thenReturn(savedSubmission)

        val result = submissionService.createSubmission(request)

        assertEquals(10L, result.id)
        assertEquals(1L, result.interviewerId)
        assertEquals(2L, result.surveyId)

        verify(interviewerRepository).findById(1L)
        verify(surveyRepository).findById(2L)
        verify(submissionRepository).save(any<Submission>())
    }

    @Test
    fun `create submission should throw when interviewer not found`() {
        val request = CreateSubmissionRequest(
                interviewerId = 99L,
                surveyId = 2L
        )

        whenever(interviewerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(InterviewerNotFoundException::class.java) {
            submissionService.createSubmission(request)
        }

        verify(interviewerRepository).findById(99L)
        verify(surveyRepository, never()).findById(any())
        verify(submissionRepository, never()).save(any<Submission>())
    }

    @Test
    fun `create submission should throw when survey not found`() {
        val request = CreateSubmissionRequest(
                interviewerId = 1L,
                surveyId = 99L
        )

        val interviewer = makeInterviewer(id = 1L)

        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))
        whenever(surveyRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyNotFoundException::class.java) {
            submissionService.createSubmission(request)
        }

        verify(interviewerRepository).findById(1L)
        verify(surveyRepository).findById(99L)
        verify(submissionRepository, never()).save(any<Submission>())
    }

    @Test
    fun `get submission by id should return response`() {
        val submission = makeSubmission(
                id = 10L,
                interviewer = makeInterviewer(id = 1L),
                survey = makeSurvey(id = 2L)
        )

        whenever(submissionRepository.findById(10L)).thenReturn(Optional.of(submission))

        val result = submissionService.getSubmissionById(10L)

        assertEquals(10L, result.id)
        assertEquals(1L, result.interviewerId)
        assertEquals(2L, result.surveyId)

        verify(submissionRepository).findById(10L)
    }

    @Test
    fun `get submission by id should throw when submission not found`() {
        whenever(submissionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubmissionNotFoundException::class.java) {
            submissionService.getSubmissionById(99L)
        }

        verify(submissionRepository).findById(99L)
    }

    @Test
    fun `get all submissions should return all responses`() {
        val submission1 = makeSubmission(
                id = 10L,
                interviewer = makeInterviewer(id = 1L),
                survey = makeSurvey(id = 2L)
        )
        val submission2 = makeSubmission(
                id = 11L,
                interviewer = makeInterviewer(id = 3L),
                survey = makeSurvey(id = 4L)
        )

        whenever(submissionRepository.findAll()).thenReturn(listOf(submission1, submission2))

        val result = submissionService.getAllSubmissions()

        assertEquals(2, result.size)
        assertEquals(10L, result[0].id)
        assertEquals(1L, result[0].interviewerId)
        assertEquals(2L, result[0].surveyId)
        assertEquals(11L, result[1].id)
        assertEquals(3L, result[1].interviewerId)
        assertEquals(4L, result[1].surveyId)

        verify(submissionRepository).findAll()
    }

    @Test
    fun `get submissions by interviewer id should return matching responses`() {
        val interviewer = makeInterviewer(id = 1L)
        val submission1 = makeSubmission(id = 10L, interviewer = interviewer, survey = makeSurvey(id = 2L))
        val submission2 = makeSubmission(id = 11L, interviewer = interviewer, survey = makeSurvey(id = 3L))

        whenever(submissionRepository.findByInterviewerId(1L)).thenReturn(listOf(submission1, submission2))

        val result = submissionService.getSubmissionsByInterviewerId(1L)

        assertEquals(2, result.size)
        assertEquals(1L, result[0].interviewerId)
        assertEquals(1L, result[1].interviewerId)

        verify(submissionRepository).findByInterviewerId(1L)
    }

    @Test
    fun `get submissions by survey id should return matching responses`() {
        val survey = makeSurvey(id = 2L)
        val submission1 = makeSubmission(id = 10L, interviewer = makeInterviewer(id = 1L), survey = survey)
        val submission2 = makeSubmission(id = 11L, interviewer = makeInterviewer(id = 3L), survey = survey)

        whenever(submissionRepository.findBySurveyId(2L)).thenReturn(listOf(submission1, submission2))

        val result = submissionService.getSubmissionsBySurveyId(2L)

        assertEquals(2, result.size)
        assertEquals(2L, result[0].surveyId)
        assertEquals(2L, result[1].surveyId)

        verify(submissionRepository).findBySurveyId(2L)
    }

    @Test
    fun `update submission should return updated response`() {
        val request = UpdateSubmissionRequest(
                interviewerId = 3L,
                surveyId = 4L
        )

        val existingSubmission = makeSubmission(
                id = 10L,
                interviewer = makeInterviewer(id = 1L),
                survey = makeSurvey(id = 2L)
        )

        val updatedInterviewer = makeInterviewer(id = 3L)
        val updatedSurvey = makeSurvey(id = 4L)

        val updatedSubmission = existingSubmission.copy(
                interviewer = updatedInterviewer,
                survey = updatedSurvey
        )

        whenever(submissionRepository.findById(10L)).thenReturn(Optional.of(existingSubmission))
        whenever(interviewerRepository.findById(3L)).thenReturn(Optional.of(updatedInterviewer))
        whenever(surveyRepository.findById(4L)).thenReturn(Optional.of(updatedSurvey))
        whenever(submissionRepository.save(any<Submission>())).thenReturn(updatedSubmission)

        val result = submissionService.updateSubmission(10L, request)

        assertEquals(10L, result.id)
        assertEquals(3L, result.interviewerId)
        assertEquals(4L, result.surveyId)

        verify(submissionRepository).findById(10L)
        verify(interviewerRepository).findById(3L)
        verify(surveyRepository).findById(4L)
        verify(submissionRepository).save(any<Submission>())
    }

    @Test
    fun `update submission should throw when submission not found`() {
        val request = UpdateSubmissionRequest(
                interviewerId = 3L,
                surveyId = 4L
        )

        whenever(submissionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubmissionNotFoundException::class.java) {
            submissionService.updateSubmission(99L, request)
        }

        verify(submissionRepository).findById(99L)
        verify(interviewerRepository, never()).findById(any())
        verify(surveyRepository, never()).findById(any())
        verify(submissionRepository, never()).save(any<Submission>())
    }

    @Test
    fun `update submission should throw when interviewer not found`() {
        val request = UpdateSubmissionRequest(
                interviewerId = 99L,
                surveyId = 4L
        )

        val existingSubmission = makeSubmission(id = 10L)

        whenever(submissionRepository.findById(10L)).thenReturn(Optional.of(existingSubmission))
        whenever(interviewerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(InterviewerNotFoundException::class.java) {
            submissionService.updateSubmission(10L, request)
        }

        verify(submissionRepository).findById(10L)
        verify(interviewerRepository).findById(99L)
        verify(surveyRepository, never()).findById(any())
        verify(submissionRepository, never()).save(any<Submission>())
    }

    @Test
    fun `update submission should throw when survey not found`() {
        val request = UpdateSubmissionRequest(
                interviewerId = 3L,
                surveyId = 99L
        )

        val existingSubmission = makeSubmission(id = 10L)
        val interviewer = makeInterviewer(id = 3L)

        whenever(submissionRepository.findById(10L)).thenReturn(Optional.of(existingSubmission))
        whenever(interviewerRepository.findById(3L)).thenReturn(Optional.of(interviewer))
        whenever(surveyRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyNotFoundException::class.java) {
            submissionService.updateSubmission(10L, request)
        }

        verify(submissionRepository).findById(10L)
        verify(interviewerRepository).findById(3L)
        verify(surveyRepository).findById(99L)
        verify(submissionRepository, never()).save(any<Submission>())
    }

    @Test
    fun `delete submission should delete successfully`() {
        val submission = makeSubmission(id = 10L)

        whenever(submissionRepository.findById(10L)).thenReturn(Optional.of(submission))

        submissionService.deleteSubmission(10L)

        verify(submissionRepository).findById(10L)
        verify(submissionRepository).delete(submission)
    }

    @Test
    fun `delete submission should throw when submission not found`() {
        whenever(submissionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubmissionNotFoundException::class.java) {
            submissionService.deleteSubmission(99L)
        }

        verify(submissionRepository).findById(99L)
        verify(submissionRepository, never()).delete(any<Submission>())
    }

    private fun makeOrganization(
            id: Long? = 1L,
            name: String = "Ministry of Health",
            code: String = "MOH"
    ) = Organization(
            id = id,
            name = name,
            code = code,
            createdAt = LocalDateTime.now()
    )

    private fun makeSurvey(
            id: Long? = 1L,
            organization: Organization = makeOrganization(),
            name: String = "Health Survey",
            description: String = "Survey description"
    ) = Survey(
            id = id,
            organization = organization,
            name = name,
            description = description,
            createdAt = LocalDateTime.now()
    )

    private fun makeInterviewer(
            id: Long? = 1L,
            name: String = "John Tan",
            email: String? = "john.tan@example.com"
    ) = Interviewer(
            id = id,
            name = name,
            email = email,
            createdAt = LocalDateTime.now()
    )

    private fun makeSubmission(
            id: Long? = 10L,
            interviewer: Interviewer = makeInterviewer(),
            survey: Survey = makeSurvey()
    ) = Submission(
            id = id,
            interviewer = interviewer,
            survey = survey,
            createdAt = LocalDateTime.now()
    )
}
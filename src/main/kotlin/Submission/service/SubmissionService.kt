package com.huskie.dwarves.submission.service

import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.submission.dto.CreateSubmissionRequest
import com.huskie.dwarves.submission.dto.SubmissionResponse
import com.huskie.dwarves.submission.dto.UpdateSubmissionRequest
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.submission.exception.SubmissionNotFoundException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import org.springframework.stereotype.Service

@Service
class SubmissionService(
        private val submissionRepository: SubmissionRepository,
        private val interviewerRepository: InterviewerRepository,
        private val surveyRepository: SurveyRepository
) {
    fun createSubmission(request: CreateSubmissionRequest): SubmissionResponse {
        val interviewer = interviewerRepository.findById(request.interviewerId)
                .orElseThrow { InterviewerNotFoundException(request.interviewerId) }

        val survey = surveyRepository.findById(request.surveyId)
                .orElseThrow { SurveyNotFoundException(request.surveyId) }

        val submission = Submission(
                interviewer = interviewer,
                survey = survey
        )

        return submissionRepository.save(submission).toResponse()
    }

    fun getSubmissionById(id: Long): SubmissionResponse {
        val submission = submissionRepository.findById(id)
                .orElseThrow { SubmissionNotFoundException(id) }

        return submission.toResponse()
    }

    fun getAllSubmissions(): List<SubmissionResponse> {
        return submissionRepository.findAll().map { it.toResponse() }
    }

    fun getSubmissionsByInterviewerId(interviewerId: Long): List<SubmissionResponse> {
        return submissionRepository.findByInterviewerId(interviewerId)
                .map { it.toResponse() }
    }

    fun getSubmissionsBySurveyId(surveyId: Long): List<SubmissionResponse> {
        return submissionRepository.findBySurveyId(surveyId)
                .map { it.toResponse() }
    }

    fun updateSubmission(id: Long, request: UpdateSubmissionRequest): SubmissionResponse {
        val existingSubmission = submissionRepository.findById(id)
                .orElseThrow { SubmissionNotFoundException(id) }

        val interviewer = interviewerRepository.findById(request.interviewerId)
                .orElseThrow { InterviewerNotFoundException(request.interviewerId) }

        val survey = surveyRepository.findById(request.surveyId)
                .orElseThrow { SurveyNotFoundException(request.surveyId) }

        val updatedSubmission = existingSubmission.copy(
                interviewer = interviewer,
                survey = survey
        )

        return submissionRepository.save(updatedSubmission).toResponse()
    }

    fun deleteSubmission(id: Long) {
        val submission = submissionRepository.findById(id)
                .orElseThrow { SubmissionNotFoundException(id) }

        submissionRepository.delete(submission)
    }

    private fun Submission.toResponse(): SubmissionResponse {
        val submissionId = this.id
                ?: throw IllegalStateException("Submission id should not be null")

        val interviewerId = this.interviewer.id
                ?: throw IllegalStateException("Interviewer id should not be null")

        val surveyId = this.survey.id
                ?: throw IllegalStateException("Survey id should not be null")

        return SubmissionResponse(
                id = submissionId,
                interviewerId = interviewerId,
                surveyId = surveyId,
                createdAt = this.createdAt
        )
    }
}
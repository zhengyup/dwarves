package com.huskie.dwarves.interviewer.service

import com.huskie.dwarves.interviewer.dto.CreateInterviewerRequest
import com.huskie.dwarves.interviewer.dto.InterviewerResponse
import com.huskie.dwarves.interviewer.dto.UpdateInterviewerRequest
import com.huskie.dwarves.interviewer.entity.Interviewer
import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import org.springframework.stereotype.Service

@Service
class InterviewerService(
        private val interviewerRepository: InterviewerRepository
) {
    fun createInterviewer(request: CreateInterviewerRequest): InterviewerResponse {
        val interviewer = Interviewer(
                name = request.name,
                email = request.email
        )

        return interviewerRepository.save(interviewer).toResponse()
    }

    fun getInterviewerById(id: Long): InterviewerResponse {
        val interviewer = interviewerRepository.findById(id)
                .orElseThrow { InterviewerNotFoundException(id) }

        return interviewer.toResponse()
    }

    fun getAllInterviewers(): List<InterviewerResponse> {
        return interviewerRepository.findAll().map { it.toResponse() }
    }

    fun updateInterviewer(id: Long, request: UpdateInterviewerRequest): InterviewerResponse {
        val existingInterviewer = interviewerRepository.findById(id)
                .orElseThrow { InterviewerNotFoundException(id) }

        val updatedInterviewer = existingInterviewer.copy(
                name = request.name,
                email = request.email
        )

        return interviewerRepository.save(updatedInterviewer).toResponse()
    }

    fun deleteInterviewer(id: Long) {
        val interviewer = interviewerRepository.findById(id)
                .orElseThrow { InterviewerNotFoundException(id) }

        interviewerRepository.delete(interviewer)
    }

    private fun Interviewer.toResponse(): InterviewerResponse {
        val interviewerId = this.id
                ?: throw IllegalStateException("Interviewer id should not be null")

        return InterviewerResponse(
                id = interviewerId,
                name = this.name,
                email = this.email,
                createdAt = this.createdAt
        )
    }
}
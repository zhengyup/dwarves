package com.huskie.dwarves.submission.controller

import com.huskie.dwarves.submission.dto.CreateSubmissionRequest
import com.huskie.dwarves.submission.dto.SubmissionResponse
import com.huskie.dwarves.submission.dto.UpdateSubmissionRequest
import com.huskie.dwarves.submission.service.SubmissionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/submissions")
class SubmissionController(
        private val submissionService: SubmissionService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSubmission(
            @RequestBody request: CreateSubmissionRequest
    ): SubmissionResponse {
        return submissionService.createSubmission(request)
    }

    @GetMapping("/{id}")
    fun getSubmissionById(
            @PathVariable id: Long
    ): SubmissionResponse {
        return submissionService.getSubmissionById(id)
    }

    @GetMapping
    fun getAllSubmissions(): List<SubmissionResponse> {
        return submissionService.getAllSubmissions()
    }

    @GetMapping("/interviewer/{interviewerId}")
    fun getSubmissionsByInterviewerId(
            @PathVariable interviewerId: Long
    ): List<SubmissionResponse> {
        return submissionService.getSubmissionsByInterviewerId(interviewerId)
    }

    @GetMapping("/survey/{surveyId}")
    fun getSubmissionsBySurveyId(
            @PathVariable surveyId: Long
    ): List<SubmissionResponse> {
        return submissionService.getSubmissionsBySurveyId(surveyId)
    }

    @PutMapping("/{id}")
    fun updateSubmission(
            @PathVariable id: Long,
            @RequestBody request: UpdateSubmissionRequest
    ): SubmissionResponse {
        return submissionService.updateSubmission(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSubmission(
            @PathVariable id: Long
    ) {
        submissionService.deleteSubmission(id)
    }
}
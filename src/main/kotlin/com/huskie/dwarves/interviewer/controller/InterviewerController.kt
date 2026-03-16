package com.huskie.dwarves.interviewer.controller

import com.huskie.dwarves.interviewer.dto.CreateInterviewerRequest
import com.huskie.dwarves.interviewer.dto.InterviewerResponse
import com.huskie.dwarves.interviewer.dto.UpdateInterviewerRequest
import com.huskie.dwarves.interviewer.service.InterviewerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/interviewers")
class InterviewerController(
        private val interviewerService: InterviewerService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createInterviewer(
            @RequestBody request: CreateInterviewerRequest
    ): InterviewerResponse {
        return interviewerService.createInterviewer(request)
    }

    @GetMapping("/{id}")
    fun getInterviewerById(
            @PathVariable id: Long
    ): InterviewerResponse {
        return interviewerService.getInterviewerById(id)
    }

    @GetMapping
    fun getAllInterviewers(): List<InterviewerResponse> {
        return interviewerService.getAllInterviewers()
    }

    @PutMapping("/{id}")
    fun updateInterviewer(
            @PathVariable id: Long,
            @RequestBody request: UpdateInterviewerRequest
    ): InterviewerResponse {
        return interviewerService.updateInterviewer(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteInterviewer(
            @PathVariable id: Long
    ) {
        interviewerService.deleteInterviewer(id)
    }
}
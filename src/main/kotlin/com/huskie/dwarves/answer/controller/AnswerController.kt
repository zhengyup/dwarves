package com.huskie.dwarves.answer.controller

import com.huskie.dwarves.answer.dto.AnswerResponse
import com.huskie.dwarves.answer.dto.CreateAnswerRequest
import com.huskie.dwarves.answer.dto.UpdateAnswerRequest
import com.huskie.dwarves.answer.service.AnswerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/answers")
class AnswerController(
        private val answerService: AnswerService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAnswer(
            @RequestBody request: CreateAnswerRequest
    ): AnswerResponse {
        return answerService.createAnswer(request)
    }

    @GetMapping("/{id}")
    fun getAnswerById(
            @PathVariable id: Long
    ): AnswerResponse {
        return answerService.getAnswerById(id)
    }

    @GetMapping
    fun getAllAnswers(): List<AnswerResponse> {
        return answerService.getAllAnswers()
    }

    @GetMapping("/submission/{submissionId}")
    fun getAnswersBySubmissionId(
            @PathVariable submissionId: Long
    ): List<AnswerResponse> {
        return answerService.getAnswersBySubmissionId(submissionId)
    }

    @GetMapping("/question/{surveyQuestionId}")
    fun getAnswersBySurveyQuestionId(
            @PathVariable surveyQuestionId: Long
    ): List<AnswerResponse> {
        return answerService.getAnswersBySurveyQuestionId(surveyQuestionId)
    }

    @PutMapping("/{id}")
    fun updateAnswer(
            @PathVariable id: Long,
            @RequestBody request: UpdateAnswerRequest
    ): AnswerResponse {
        return answerService.updateAnswer(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAnswer(
            @PathVariable id: Long
    ) {
        answerService.deleteAnswer(id)
    }
}
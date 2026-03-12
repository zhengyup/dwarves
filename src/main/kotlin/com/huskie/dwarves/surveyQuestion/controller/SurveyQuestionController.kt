package com.huskie.dwarves.surveyQuestion.controller

import com.huskie.dwarves.surveyQuestion.dto.CreateSurveyQuestionRequest
import com.huskie.dwarves.surveyQuestion.dto.SurveyQuestionResponse
import com.huskie.dwarves.surveyQuestion.dto.UpdateSurveyQuestionRequest
import com.huskie.dwarves.surveyQuestion.service.SurveyQuestionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/survey")
class SurveyQuestionController (
    private val surveyQuestionService : SurveyQuestionService
) {
    @PostMapping
    fun createSurveyQuestion(
            @Valid @RequestBody request : CreateSurveyQuestionRequest
    ) : SurveyQuestionResponse {
        return surveyQuestionService.createSurveyQuestion(request)
    }

    @GetMapping
    fun getAllSurveyQuestions() : List<SurveyQuestionResponse> {
        return surveyQuestionService.getAllSurveyQuestions()
    }

    @GetMapping("/{surveyId}")
    fun getSurveyQuestionsBySurveyId(@PathVariable id : Long) : List<SurveyQuestionResponse> {
        return surveyQuestionService.getSurveyQuestionsBySurveyId(id)
    }

    @PutMapping("/{id}")
    fun updateSurveyQuestion(
            @PathVariable id: Long,
            @Valid @RequestBody request : UpdateSurveyQuestionRequest
    ) : SurveyQuestionResponse {
        return surveyQuestionService.updateSurveyQuestion(request)
    }

    @DeleteMapping("/{id}")
    fun deleteSurveyQuestion(
            @PathVariable id: Long
    ) {
        surveyQuestionService.deleteSurveyQuestionById(id)
    }
}
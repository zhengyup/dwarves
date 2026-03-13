package com.huskie.dwarves.surveyquestion.controller

import com.huskie.dwarves.surveyquestion.dto.CreateSurveyQuestionRequest
import com.huskie.dwarves.surveyquestion.dto.SurveyQuestionResponse
import com.huskie.dwarves.surveyquestion.dto.UpdateSurveyQuestionRequest
import com.huskie.dwarves.surveyquestion.service.SurveyQuestionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/surveyQuestion")
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
        return surveyQuestionService.updateSurveyQuestion(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteSurveyQuestion(
            @PathVariable id: Long
    ) {
        surveyQuestionService.deleteSurveyQuestionById(id)
    }
}
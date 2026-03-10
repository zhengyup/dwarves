package com.huskie.dwarves.survey.controller

import com.huskie.dwarves.survey.dto.CreateSurveyRequest
import com.huskie.dwarves.survey.dto.SurveyResponse
import com.huskie.dwarves.survey.dto.UpdateSurveyRequest
import com.huskie.dwarves.survey.service.SurveyService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/survey")
class SurveyController (
    private val surveyService: SurveyService
) {
    @PostMapping
    fun createSurvey(
            @Valid @RequestBody request : CreateSurveyRequest
    ) : SurveyResponse {
        return surveyService.createSurvey(request)
    }

    @GetMapping
    fun getAllSurveys() : List<SurveyResponse> {
        return surveyService.getAllSurveys()
    }

    @GetMapping("/{id}")
    fun getSurveyById(@PathVariable id: Long) : SurveyResponse {
        return surveyService.getSurveyById(id)
    }

    @PutMapping("/{id}")
    fun updateSurvey(
            @PathVariable id : Long,
            @Valid @RequestBody request : UpdateSurveyRequest
    ) : SurveyResponse {
        return surveyService.updateSurvey(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteSurvey(@PathVariable id : Long) {
        return surveyService.deleteSurvey(id)
    }
}
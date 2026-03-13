package com.huskie.dwarves.surveyoption.controller

import com.huskie.dwarves.surveyoption.SurveyOptionService
import com.huskie.dwarves.surveyoption.dto.CreateSurveyOptionRequest
import com.huskie.dwarves.surveyoption.dto.SurveyOptionResponse
import com.huskie.dwarves.surveyoption.dto.UpdateSurveyOptionRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/survey-options")
class SurveyOptionController(
        private val surveyOptionService: SurveyOptionService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSurveyOption(
            @RequestBody request: CreateSurveyOptionRequest
    ): SurveyOptionResponse {
        return surveyOptionService.createSurveyOption(request)
    }

    @GetMapping("/{id}")
    fun getSurveyOptionById(
            @PathVariable id: Long
    ): SurveyOptionResponse {
        return surveyOptionService.getSurveyOptionById(id)
    }

    @GetMapping("/question/{surveyQuestionId}")
    fun getSurveyOptionsBySurveyQuestionId(
            @PathVariable surveyQuestionId: Long
    ): List<SurveyOptionResponse> {
        return surveyOptionService.getSurveyOptionsBySurveyQuestionId(surveyQuestionId)
    }

    @PutMapping("/{id}")
    fun updateSurveyOption(
            @PathVariable id: Long,
            @RequestBody request: UpdateSurveyOptionRequest
    ): SurveyOptionResponse {
        return surveyOptionService.updateSurveyOption(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSurveyOption(
            @PathVariable id: Long
    ) {
        surveyOptionService.deleteSurveyOption(id)
    }
}
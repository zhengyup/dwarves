package com.huskie.dwarves.surveyoption

import com.huskie.dwarves.surveyoption.dto.CreateSurveyOptionRequest
import com.huskie.dwarves.surveyoption.dto.SurveyOptionResponse
import com.huskie.dwarves.surveyoption.dto.UpdateSurveyOptionRequest
import com.huskie.dwarves.surveyoption.entity.SurveyOption
import com.huskie.dwarves.surveyoption.exceptions.DuplicateSurveyOptionDisplayOrderException
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.springframework.stereotype.Service

@Service
class SurveyOptionService(
        private val surveyOptionRepository: SurveyOptionRepository,
        private val surveyQuestionRepository: SurveyQuestionRepository
) {
    fun createSurveyOption(request: CreateSurveyOptionRequest): SurveyOptionResponse {
        surveyQuestionRepository.findById(request.surveyQuestionId)
                .orElseThrow {
                    SurveyQuestionNotFoundException(request.surveyQuestionId)
                }

        if (surveyOptionRepository.existsBySurveyQuestionIdAndDisplayOrder(
                        request.surveyQuestionId,
                        request.displayOrder
                )
        ) {
            throw DuplicateSurveyOptionDisplayOrderException(
                    "Display order ${request.displayOrder} already exists for survey question ${request.surveyQuestionId}"
            )
        }

        val savedOption = surveyOptionRepository.save(
                SurveyOption(
                        surveyQuestionId = request.surveyQuestionId,
                        optionText = request.optionText,
                        optionValue = request.optionValue,
                        displayOrder = request.displayOrder
                )
        )

        return savedOption.toResponse()
    }

    fun getSurveyOptionById(id: Long): SurveyOptionResponse {
        val option = surveyOptionRepository.findById(id)
                .orElseThrow {
                    SurveyOptionNotFoundException("Survey option with id $id not found")
                }

        return option.toResponse()
    }

    fun getSurveyOptionsBySurveyQuestionId(surveyQuestionId: Long): List<SurveyOptionResponse> {
        surveyQuestionRepository.findById(surveyQuestionId)
                .orElseThrow {
                    SurveyQuestionNotFoundException(surveyQuestionId)
                }

        return surveyOptionRepository.findBySurveyQuestionIdOrderByDisplayOrderAsc(surveyQuestionId)
                .map { it.toResponse() }
    }

    fun updateSurveyOption(id: Long, request: UpdateSurveyOptionRequest): SurveyOptionResponse {
        val existingOption = surveyOptionRepository.findById(id)
                .orElseThrow {
                    SurveyOptionNotFoundException("Survey option with id $id not found")
                }

        val conflictingOption = surveyOptionRepository
                .findBySurveyQuestionIdOrderByDisplayOrderAsc(existingOption.surveyQuestionId)
                .firstOrNull { it.displayOrder == request.displayOrder && it.id != id }

        if (conflictingOption != null) {
            throw DuplicateSurveyOptionDisplayOrderException(
                    "Display order ${request.displayOrder} already exists for survey question ${existingOption.surveyQuestionId}"
            )
        }

        val updatedOption = existingOption.copy(
                optionText = request.optionText,
                optionValue = request.optionValue,
                displayOrder = request.displayOrder
        )

        return surveyOptionRepository.save(updatedOption).toResponse()
    }

    fun deleteSurveyOption(id: Long) {
        val option = surveyOptionRepository.findById(id)
                .orElseThrow {
                    SurveyOptionNotFoundException("Survey option with id $id not found")
                }

        surveyOptionRepository.delete(option)
    }

    private fun SurveyOption.toResponse(): SurveyOptionResponse {
        return SurveyOptionResponse(
                id = this.id,
                surveyQuestionId = this.surveyQuestionId,
                optionText = this.optionText,
                optionValue = this.optionValue,
                displayOrder = this.displayOrder,
                createdAt = this.createdAt
        )
    }
}
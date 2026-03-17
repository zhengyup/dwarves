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
        val question = surveyQuestionRepository.findById(request.surveyQuestionId)
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
                        surveyQuestion = question,
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
                    SurveyOptionNotFoundException(id)
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
                    SurveyOptionNotFoundException(id)
                }

        val surveyQuestionId = existingOption.surveyQuestion.id
                ?: throw IllegalStateException("Survey question id should not be null")

        val conflictingOption = surveyOptionRepository
                .findBySurveyQuestionIdOrderByDisplayOrderAsc(surveyQuestionId)
                .firstOrNull { it.displayOrder == request.displayOrder && it.id != id }

        if (conflictingOption != null) {
            throw DuplicateSurveyOptionDisplayOrderException(
                    "Display order ${request.displayOrder} already exists for survey question " +
                            "${existingOption.surveyQuestion.id}"
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
                    SurveyOptionNotFoundException(id)
                }

        surveyOptionRepository.delete(option)
    }

    private fun SurveyOption.toResponse(): SurveyOptionResponse {
        val surveyQuestionId = this.surveyQuestion.id
                ?: throw IllegalStateException("Survey question id should not be null")

        return SurveyOptionResponse(
                id = this.id,
                surveyQuestionId = surveyQuestionId,
                optionText = this.optionText,
                optionValue = this.optionValue,
                displayOrder = this.displayOrder,
                createdAt = this.createdAt
        )
    }
}
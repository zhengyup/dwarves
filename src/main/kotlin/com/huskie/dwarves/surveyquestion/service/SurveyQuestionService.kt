package com.huskie.dwarves.surveyquestion.service

import com.huskie.dwarves.surveyquestion.dto.UpdateSurveyQuestionRequest
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyoption.exceptions.DuplicateSurveyOptionDisplayOrderException
import com.huskie.dwarves.surveyquestion.dto.CreateSurveyQuestionRequest
import com.huskie.dwarves.surveyquestion.dto.SurveyQuestionResponse
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyquestion.exceptions.DuplicateSurveyQuestionDisplayOrderException
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.springframework.stereotype.Service

@Service
class SurveyQuestionService (
        private val surveyRepository: SurveyRepository,
        private val surveyQuestionRepository: SurveyQuestionRepository,

) {
    fun createSurveyQuestion(createSurveyQuestionRequest: CreateSurveyQuestionRequest) : SurveyQuestionResponse {
        val reqSurvey = surveyRepository.findById(createSurveyQuestionRequest.surveyId)
                .orElseThrow{SurveyNotFoundException(createSurveyQuestionRequest.surveyId)}

        if (surveyQuestionRepository.existsBySurveyIdAndDisplayOrder(
                        createSurveyQuestionRequest.surveyId,
                        createSurveyQuestionRequest.displayOrder
                )
        ) {
            throw DuplicateSurveyQuestionDisplayOrderException(
                    createSurveyQuestionRequest.surveyId,
                    createSurveyQuestionRequest.displayOrder
            )
        }

        val surveyQuestion = SurveyQuestion(
                survey = reqSurvey,
                questionText = createSurveyQuestionRequest.questionText,
                questionType = createSurveyQuestionRequest.questionType,
                isRequired = createSurveyQuestionRequest.isRequired,
                displayOrder = createSurveyQuestionRequest.displayOrder
        )
        val saved = surveyQuestionRepository.save(surveyQuestion)
        return saved.toResponse()
    }

    fun getAllSurveyQuestions() : List<SurveyQuestionResponse> {
        return surveyQuestionRepository.findAll().map{ it.toResponse() }
    }

    fun getSurveyQuestionsBySurveyId(surveyId: Long) : List<SurveyQuestionResponse>{
        surveyRepository.findById(surveyId).orElseThrow{SurveyNotFoundException(surveyId)}
        return surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(surveyId)
                .map { it.toResponse() }
    }

    fun updateSurveyQuestion(id: Long, updateSurveyQuestionRequest: UpdateSurveyQuestionRequest) :
            SurveyQuestionResponse{
        val existing = surveyQuestionRepository.findById(id)
                .orElseThrow{SurveyQuestionNotFoundException(id)}
        val reqSurvey = surveyRepository.findById(updateSurveyQuestionRequest.surveyId)
                .orElseThrow{SurveyNotFoundException(updateSurveyQuestionRequest.surveyId)}

        val surveyQuestionId = existing.survey.id
                ?: throw IllegalStateException("Survey id should not be null")

        val conflictingOption = surveyQuestionRepository
                .findBySurveyIdOrderByDisplayOrderAsc(surveyQuestionId)
                .firstOrNull { it.displayOrder == updateSurveyQuestionRequest.displayOrder && it.id != id }

        if (conflictingOption != null) {
            throw DuplicateSurveyQuestionDisplayOrderException(
                    surveyQuestionId,
                    updateSurveyQuestionRequest.displayOrder
            )
        }

        val surveyQuestion = SurveyQuestion(
                id = existing.id,
                survey = reqSurvey,
                questionText = updateSurveyQuestionRequest.questionText,
                questionType = updateSurveyQuestionRequest.questionType,
                isRequired = updateSurveyQuestionRequest.isRequired,
                displayOrder = updateSurveyQuestionRequest.displayOrder,
                createdAt = existing.createdAt
        )
        val saved = surveyQuestionRepository.save(surveyQuestion)
        return saved.toResponse()
    }

    fun deleteSurveyQuestionById(id : Long) {
        val surveyQuestionToDelete = surveyQuestionRepository.findById(id)
                .orElseThrow{SurveyQuestionNotFoundException(id)}
        surveyQuestionRepository.delete(surveyQuestionToDelete)
    }

    private fun SurveyQuestion.toResponse(): SurveyQuestionResponse {
        return SurveyQuestionResponse(
                id = requireNotNull(this.id),
                surveyId = requireNotNull(this.survey.id),
                questionText = this.questionText,
                questionType = this.questionType,
                isRequired = this.isRequired,
                displayOrder = this.displayOrder
        )
    }
}

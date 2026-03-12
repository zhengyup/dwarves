package com.huskie.dwarves.survey.service

import com.huskie.dwarves.organization.exceptions.OrganizationNotFoundException
import com.huskie.dwarves.organization.repository.OrganizationRepository
import com.huskie.dwarves.survey.dto.CreateSurveyRequest
import com.huskie.dwarves.survey.dto.SurveyResponse
import com.huskie.dwarves.survey.dto.UpdateSurveyRequest
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import org.springframework.stereotype.Service

@Service
class SurveyService (
        private val surveyRepository : SurveyRepository,
        private val organizationRepository: OrganizationRepository
) {

    fun createSurvey(request: CreateSurveyRequest) : SurveyResponse {
        val reqOrganization = organizationRepository.findById(request.organizationId)
                .orElseThrow{OrganizationNotFoundException(request.organizationId)}
        val survey = Survey(
                name = request.name,
                organization = reqOrganization,
                description = request.description
        )
        val saved = surveyRepository.save(survey)
        return saved.toResponse()
    }

    fun getAllSurveys() : List<SurveyResponse> {
        return surveyRepository.findAll().map{ it.toResponse() }
    }

    fun getSurveyById(id: Long): SurveyResponse {
        val survey = surveyRepository.findById(id)
                .orElseThrow{ SurveyNotFoundException(id) }
        return survey.toResponse()
    }

    fun updateSurvey(id : Long, request: UpdateSurveyRequest) : SurveyResponse {
        val existing = surveyRepository.findById(id)
                .orElseThrow{SurveyNotFoundException(id)}
        val organization = organizationRepository.findById(request.organizationId)
                .orElseThrow{OrganizationNotFoundException(request.organizationId)}
        val updated = Survey(
                id = existing.id,
                organization = organization,
                name = request.name,
                description = request.description,
                createdAt = existing.createdAt
        )
        val saved = surveyRepository.save(updated)
        return saved.toResponse()
    }

    fun deleteSurvey(id : Long) {
        val existing = surveyRepository.findById(id)
                .orElseThrow{ SurveyNotFoundException(id) }
        surveyRepository.delete(existing)
    }

    private fun Survey.toResponse(): SurveyResponse {
        return SurveyResponse(
                id = requireNotNull(this.id),
                organizationId = requireNotNull(this.organization.id),
                name = this.name,
                description = this.description,
        )
    }
}
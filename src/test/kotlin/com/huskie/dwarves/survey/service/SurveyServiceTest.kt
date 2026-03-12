package com.huskie.dwarves.survey.service

import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.organization.exceptions.OrganizationNotFoundException
import com.huskie.dwarves.organization.repository.OrganizationRepository
import com.huskie.dwarves.survey.dto.CreateSurveyRequest
import com.huskie.dwarves.survey.dto.UpdateSurveyRequest
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

class SurveyServiceTest {
    private val surveyRepository: SurveyRepository = mock()
    private val organizationRepository: OrganizationRepository = mock()
    private val surveyService: SurveyService = SurveyService(surveyRepository, organizationRepository)
    private val mockOrganization = Organization(
            id = 1L,
            name = "Ministry of Health",
            code = "MOH",
            createdAt = LocalDateTime.now()
    )
    @Test
    fun `create survey should return saved response`() {
        val request = CreateSurveyRequest(
                name = "Diabetes knowledge in youth",
                organizationId = 1L,
                description = "Survey youths between 12 - 25 about common diabetes knowledge"
        )



        val savedSurvey = Survey(
                id = 1L,
                name = "Diabetes knowledge in youth",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common diabetes knowledge",
                createdAt = LocalDateTime.now()
        )

        whenever(organizationRepository.findById(1L)).thenReturn(Optional.of(mockOrganization))
        whenever(surveyRepository.save(any<Survey>())).thenReturn(savedSurvey)
        val result = surveyService.createSurvey(request)

        assertEquals(savedSurvey.id, result.id)
        assertEquals(savedSurvey.name, result.name)
        assertEquals(savedSurvey.organization.id, result.organizationId)
        assertEquals(savedSurvey.description, result.description)
        verify(surveyRepository, times(1)).save(any())
    }


    @Test
    fun `create survey should throw when organization not found`() {
        val request = CreateSurveyRequest(
                name = "Diabetes knowledge in youth",
                organizationId = 999L,
                description = "Survey youths between 12 - 25 about common diabetes knowledge"
        )


        whenever(organizationRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(OrganizationNotFoundException::class.java) {
            surveyService.createSurvey(request)
        }

        verify(organizationRepository, times(1)).findById(999L)
    }

    @Test
    fun `get survey by id should return survey when found`() {
        val existingSurvey = Survey(
                id = 1L,
                name = "Diabetes knowledge in youth",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common diabetes knowledge",
                createdAt = LocalDateTime.now()
        )

        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(existingSurvey))

        val result = surveyService.getSurveyById(1L)

        assertEquals(existingSurvey.id, result.id)
        assertEquals(existingSurvey.name, result.name)
        assertEquals(existingSurvey.organization.id, result.organizationId)
        assertEquals(existingSurvey.description, result.description)
        verify(surveyRepository, times(1)).findById(1L)
    }

    @Test
    fun `get survey by id should throw when survey not found`() {
        whenever(surveyRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(SurveyNotFoundException::class.java) {
            surveyService.getSurveyById(999L)
        }

        verify(surveyRepository, times(1)).findById(999L)
    }

    @Test
    fun `update survey should update fields and return updated response`() {
        val existingSurvey = Survey(
                id = 1L,
                name = "Diabetes knowledge in youth",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common diabetes knowledge",
                createdAt = LocalDateTime.now()
        )

        val request = UpdateSurveyRequest(
            name = "Dementia knowledge in youth",
            organizationId = 2L,
            description = "Survey on dementia",
        )

        val updatedOrganization = Organization(
                id = 2L,
                name = "Centre for Active Aging",
                code = "CAA",
                createdAt = LocalDateTime.now()
        )

        val updatedSurvey = Survey(
                id = 1L,
                name = "Dementia knowledge in youth",
                organization = updatedOrganization,
                description = "Survey on dementia",
                createdAt = existingSurvey.createdAt
        )

        whenever(organizationRepository.findById(2L)).thenReturn(Optional.of(updatedOrganization))
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(existingSurvey))
        whenever(surveyRepository.save(any<Survey>())).thenReturn(updatedSurvey)

        val result = surveyService.updateSurvey(1L, request)

        assertEquals(updatedSurvey.id, result.id)
        assertEquals(updatedSurvey.name, result.name)
        assertEquals(updatedSurvey.organization.id, result.organizationId)
        assertEquals(updatedSurvey.description, result.description)
        verify(surveyRepository, times(1)).findById(1L)
        verify(surveyRepository, times(1)).save(any())
        verify(organizationRepository, times(1)).findById(2L)
    }

    @Test
    fun `update survey throws error when survey does not exist`() {
        val request = UpdateSurveyRequest(
                name = "Dementia knowledge in youth",
                organizationId = 2L,
                description = "Survey on dementia",
        )
        whenever(surveyRepository.findById(2L)).thenReturn(Optional.empty())
        assertThrows(SurveyNotFoundException::class.java) {
            surveyService.updateSurvey(2L, request)
        }
        verify(surveyRepository, times(1)).findById(2L)
    }

    @Test
    fun `update survey throws error when organization does not exist`() {

        val existingSurvey = Survey(
                id = 1L,
                name = "Diabetes knowledge in youth",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common diabetes knowledge",
                createdAt = LocalDateTime.now()
        )

        val request = UpdateSurveyRequest(
                name = "Dementia knowledge in youth",
                organizationId = 2L,
                description = "Survey on dementia",
        )
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(existingSurvey))
        whenever(organizationRepository.findById(2L)).thenReturn(Optional.empty())
        assertThrows(OrganizationNotFoundException::class.java) {
            surveyService.updateSurvey(1L, request)
        }
        verify(organizationRepository, times(1)).findById(2L)
    }

    @Test
    fun `delete survey should delete existing survey`() {
        val existingSurvey = Survey(
                id = 1L,
                name = "Diabetes knowledge in youth",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common diabetes knowledge",
                createdAt = LocalDateTime.now()
        )

        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(existingSurvey))

        surveyService.deleteSurvey(1L)

        verify(surveyRepository, times(1)).findById(1L)
        verify(surveyRepository, times(1)).delete(existingSurvey)
    }

    @Test
    fun `delete survey should throw when survey not found`() {
        whenever(surveyRepository.findById(2L)).thenReturn(Optional.empty())
        assertThrows(SurveyNotFoundException::class.java) {
            surveyService.deleteSurvey(2L)
        }
        verify(surveyRepository, times(1)).findById(2L)
    }

    @Test
    fun `get all surveys should return list of survey responses`() {
        val surveys = listOf(
                Survey(
                    id = 1L,
                    name = "Diabetes knowledge in youth",
                    organization = mockOrganization,
                    description = "Survey youths between 12 - 25 about common diabetes knowledge",
                    createdAt = LocalDateTime.now()
                ),
                Survey(
                    id = 2L,
                    name = "Dementia knowledge in youth",
                    organization = mockOrganization,
                    description = "Survey on dementia",
                    createdAt = LocalDateTime.now()
            )
        )

        whenever(surveyRepository.findAll()).thenReturn(surveys)

        val result = surveyService.getAllSurveys()

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(2L, result[1].id)
        verify(surveyRepository, times(1)).findAll()
    }
}


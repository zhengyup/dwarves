package com.huskie.dwarves.surveyQuestion.service

import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyQuestion.dto.CreateSurveyQuestionRequest
import com.huskie.dwarves.surveyQuestion.dto.SurveyQuestionResponse
import com.huskie.dwarves.surveyQuestion.dto.UpdateSurveyQuestionRequest
import com.huskie.dwarves.surveyQuestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyQuestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyQuestion.repository.SurveyQuestionRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class SurveyQuestionServiceTest {
    private val surveyRepository: SurveyRepository = mock()
    private val surveyQuestionRepository: SurveyQuestionRepository = mock()
    private val surveyQuestionService = SurveyQuestionService(surveyRepository, surveyQuestionRepository)
    private val mockOrganization = Organization(
            id = 1L,
            name = "Ministry of Health",
            code = "MOH",
            createdAt = LocalDateTime.now()
    )
    private val mockSurvey = Survey(
            id = 1L,
            name = "Diabetes knowledge in youth",
            organization = mockOrganization,
            description = "Survey youths between 12 - 25 about common diabetes knowledge",
            createdAt = LocalDateTime.now()
    )

    private fun assertEqualsExpectedReceived(question:SurveyQuestion, response: SurveyQuestionResponse){
        assertEquals(question.id, response.id)
        assertEquals(question.isRequired, response.isRequired)
        assertEquals(question.questionText, response.questionText)
        assertEquals(question.questionType, response.questionType)
        assertEquals(question.displayOrder, response.displayOrder)
    }

    @Test
    fun `create survey question should return saved response`() {
        val request = CreateSurveyQuestionRequest(
                surveyId = 1L,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )

        val savedQuestion = SurveyQuestion(
                id = 1L,
                survey = mockSurvey,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )

        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(mockSurvey))
        whenever(surveyQuestionRepository.save(any<SurveyQuestion>())).thenReturn(savedQuestion)

        val result = surveyQuestionService.createSurveyQuestion(request)
        assertEqualsExpectedReceived(savedQuestion, result)
        verify(surveyQuestionRepository, times(1)).save(any())
        verify(surveyRepository, times(1)).findById(1L)
    }

    @Test
    fun `create survey question should throw error when survey not found`() {
        val request = CreateSurveyQuestionRequest(
                surveyId = 1L,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )

        whenever(surveyRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows(SurveyNotFoundException::class.java) {
            surveyQuestionService.createSurveyQuestion(request)
        }
        verify(surveyRepository, times(1)).findById(1L)
    }

    @Test
    fun `get survey question by id should return survey questions in order for a survey`() {
        val surveyQuestions = listOf<SurveyQuestion>(
               SurveyQuestion(id = 1L,  survey = mockSurvey,  questionType = "Open-ended",
                       isRequired = true,  displayOrder = 1,  questionText = "How many types of diabetes " +
                       "are there"
                ),
                SurveyQuestion(id = 2L,  survey = mockSurvey,  questionType = "Open-ended",
                        isRequired = true,  displayOrder = 2,  questionText = "What Percentage of " +
                        "Singaporeans suffer from diabetes"
                )
        )
        whenever(surveyRepository.findById(1)).thenReturn(Optional.of(mockSurvey))
        whenever(surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(1L)).thenReturn(surveyQuestions)

        val result = surveyQuestionService.getSurveyQuestionsBySurveyId(1L)

        assertEquals(result.size, 2)
        assertTrue(
                result.zipWithNext().all { (a, b) -> a.displayOrder <= b.displayOrder }
        )
        verify(surveyQuestionRepository, times(1)).findBySurveyIdOrderByDisplayOrderAsc(1L)
    }

    @Test
    fun `get survey questions by survey id should throw error if survey not found`() {
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows(SurveyNotFoundException::class.java) {
            surveyQuestionService.getSurveyQuestionsBySurveyId(1L)
        }
        verify(surveyRepository, times(1)).findById(1L)
    }

    @Test
    fun `get all survey questions should return all survey questions`() {
        val surveyQuestions = listOf<SurveyQuestion>(
                SurveyQuestion(id = 1L,  survey = mockSurvey,  questionType = "Open-ended",
                        isRequired = true,  displayOrder = 1,  questionText = "How many types of diabetes " +
                        "are there"
                ),
                SurveyQuestion(id = 2L,  survey = mockSurvey,  questionType = "Open-ended",
                        isRequired = true,  displayOrder = 2,  questionText = "What Percentage of " +
                        "Singaporeans suffer from diabetes"
                )
        )
        whenever(surveyQuestionRepository.findAll()).thenReturn(surveyQuestions)
        val result = surveyQuestionService.getAllSurveyQuestions()
        assertEquals(result.size, 2)
        assertEquals(result[0].id, 1L)
        assertEquals(result[1].id, 2L)
        verify(surveyQuestionRepository, times(1)).findAll()
    }

    @Test
    fun `update survey should return updated survey`() {
        val existingQuestion = SurveyQuestion(
                id = 1L,
                survey = mockSurvey,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )
        val replacementSurvey = Survey(
                id = 2L,
                name = "Dementia Knowledge",
                organization = mockOrganization,
                description = "Survey youths between 12 - 25 about common dementia knowledge",
                createdAt = LocalDateTime.now()
        )
        val savedQuestion = SurveyQuestion(
                id = 1L,
                survey = replacementSurvey,
                questionType = "MRQ",
                isRequired = false,
                displayOrder = 2,
                questionText = "What percentage of adults have diabetes in Singapore"
        )
        val request = UpdateSurveyQuestionRequest(
                surveyId = 2L,
                questionType = "MRQ",
                isRequired = false,
                displayOrder = 2,
                questionText = "What percentage of adults have diabetes in Singapore"
        )
        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion))
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(mockSurvey))
        whenever(surveyRepository.findById(2L)).thenReturn(Optional.of(replacementSurvey))
        whenever(surveyQuestionRepository.save(any<SurveyQuestion>())).thenReturn(savedQuestion)


        val result = surveyQuestionService.updateSurveyQuestion(1L, request)
        assertEqualsExpectedReceived(savedQuestion, result)
        verify(surveyQuestionRepository, times(1)).findById(1L)
        verify(surveyRepository, times(1)).findById(2L)
    }

    @Test
    fun `update survey should throw when updated survey does not exist`() {
        val existingQuestion = SurveyQuestion(
                id = 1L,
                survey = mockSurvey,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )
        val request = UpdateSurveyQuestionRequest(
                surveyId = 2L,
                questionType = "MRQ",
                isRequired = false,
                displayOrder = 2,
                questionText = "What percentage of adults have diabetes in Singapore"
        )
        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion))
        whenever(surveyRepository.findById(1L)).thenReturn(Optional.of(mockSurvey))
        whenever(surveyRepository.findById(2L)).thenReturn(Optional.empty())

        assertThrows(SurveyNotFoundException::class.java) {
            surveyQuestionService.updateSurveyQuestion(1L, request)
        }
        verify(surveyRepository, times(1)).findById(2L)
    }

    @Test
    fun `update survey should throw when survey question does not exist`() {
        val request = UpdateSurveyQuestionRequest(
                surveyId = 2L,
                questionType = "MRQ",
                isRequired = false,
                displayOrder = 2,
                questionText = "What percentage of adults have diabetes in Singapore"
        )

        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(SurveyQuestionNotFoundException::class.java) {
            surveyQuestionService.updateSurveyQuestion(1L, request)
        }
        verify(surveyQuestionRepository, times(1)).findById(1L)
    }

    @Test
    fun `delete survey question should delete question`() {
        val existingQuestion = SurveyQuestion(
                id = 1L,
                survey = mockSurvey,
                questionType = "Open-ended",
                isRequired = true,
                displayOrder = 1,
                questionText = "How many types of diabetes are there"
        )
        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion))
        surveyQuestionService.deleteSurveyQuestionById(1L)
        verify(surveyQuestionRepository, times(1)).findById(1L)
        verify(surveyQuestionRepository, times(1)).delete(any())
    }

    @Test
    fun `delete survey question should throw when survey question not found`() {
        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows(SurveyQuestionNotFoundException::class.java) {
            surveyQuestionService.deleteSurveyQuestionById(1L)
        }
        verify(surveyQuestionRepository, times(1)).findById(1L)
    }
}
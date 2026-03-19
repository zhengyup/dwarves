package com.huskie.dwarves.surveyoption


import com.huskie.dwarves.surveyoption.dto.CreateSurveyOptionRequest
import com.huskie.dwarves.surveyoption.dto.UpdateSurveyOptionRequest
import com.huskie.dwarves.surveyoption.entity.SurveyOption
import com.huskie.dwarves.surveyoption.exceptions.DuplicateSurveyOptionDisplayOrderException
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import com.huskie.dwarves.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

class SurveyOptionServiceTest {

    private val surveyOptionRepository: SurveyOptionRepository = mock()
    private val surveyQuestionRepository: SurveyQuestionRepository = mock()
    private val surveyOptionService = SurveyOptionService(
            surveyOptionRepository,
            surveyQuestionRepository
    )

    @Test
    fun `create survey option should return saved response`() {
        val request = CreateSurveyOptionRequest(
                surveyQuestionId = 1L,
                optionText = "Yes",
                optionValue = "YES",
                displayOrder = 1
        )

        val surveyQuestion = makeSurveyQuestion(id = 1L)
        val savedOption = makeSurveyOption(
                id = 1L,
                surveyQuestion = surveyQuestion,
                optionText = "Yes",
                optionValue = "YES",
                displayOrder = 1
        )

        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(surveyQuestion))
        whenever(surveyOptionRepository.existsBySurveyQuestionIdAndDisplayOrder(1L, 1)).thenReturn(false)
        whenever(surveyOptionRepository.save(any<SurveyOption>())).thenReturn(savedOption)

        val result = surveyOptionService.createSurveyOption(request)

        assertEquals(1L, result.id)
        assertEquals(1L, result.surveyQuestionId)
        assertEquals("Yes", result.optionText)
        assertEquals("YES", result.optionValue)
        assertEquals(1, result.displayOrder)

        verify(surveyQuestionRepository).findById(1L)
        verify(surveyOptionRepository).existsBySurveyQuestionIdAndDisplayOrder(1L, 1)
        verify(surveyOptionRepository).save(any<SurveyOption>())
    }

    @Test
    fun `create survey option should throw when survey question not found`() {
        val request = CreateSurveyOptionRequest(
                surveyQuestionId = 99L,
                optionText = "Yes",
                optionValue = "YES",
                displayOrder = 1
        )

        whenever(surveyQuestionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyQuestionNotFoundException::class.java) {
            surveyOptionService.createSurveyOption(request)
        }

        verify(surveyQuestionRepository).findById(99L)
        verify(surveyOptionRepository, never()).save(any<SurveyOption>())
    }

    @Test
    fun `create survey option should throw when display order already exists`() {
        val request = CreateSurveyOptionRequest(
                surveyQuestionId = 1L,
                optionText = "Yes",
                optionValue = "YES",
                displayOrder = 1
        )

        val surveyQuestion = makeSurveyQuestion(id = 1L)

        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(surveyQuestion))
        whenever(surveyOptionRepository.existsBySurveyQuestionIdAndDisplayOrder(1L, 1)).thenReturn(true)

        assertThrows(DuplicateSurveyOptionDisplayOrderException::class.java) {
            surveyOptionService.createSurveyOption(request)
        }

        verify(surveyQuestionRepository).findById(1L)
        verify(surveyOptionRepository).existsBySurveyQuestionIdAndDisplayOrder(1L, 1)
        verify(surveyOptionRepository, never()).save(any<SurveyOption>())
    }

    @Test
    fun `get survey option by id should return response`() {
        val option = makeSurveyOption(
                id = 1L,
                optionText = "No",
                optionValue = "NO",
                displayOrder = 2
        )

        whenever(surveyOptionRepository.findById(1L)).thenReturn(Optional.of(option))

        val result = surveyOptionService.getSurveyOptionById(1L)

        assertEquals(1L, result.id)
        assertEquals(option.surveyQuestion.id, result.surveyQuestionId)
        assertEquals("No", result.optionText)
        assertEquals("NO", result.optionValue)
        assertEquals(2, result.displayOrder)

        verify(surveyOptionRepository).findById(1L)
    }

    @Test
    fun `get survey option by id should throw when option not found`() {
        whenever(surveyOptionRepository.findById(2L)).thenReturn(Optional.empty())

        assertThrows(SurveyOptionNotFoundException::class.java) {
            surveyOptionService.getSurveyOptionById(2L)
        }

        verify(surveyOptionRepository).findById(2L)
    }

    @Test
    fun `get survey options by survey question id should return options in ascending display order`() {
        val surveyQuestion = makeSurveyQuestion(id = 1L)
        val option1 = makeSurveyOption(
                id = 1L,
                surveyQuestion = surveyQuestion,
                optionText = "Yes",
                optionValue = "YES",
                displayOrder = 1
        )
        val option2 = makeSurveyOption(
                id = 2L,
                surveyQuestion = surveyQuestion,
                optionText = "No",
                optionValue = "NO",
                displayOrder = 2
        )

        whenever(surveyQuestionRepository.findById(1L)).thenReturn(Optional.of(surveyQuestion))
        whenever(surveyOptionRepository.findBySurveyQuestionIdOrderByDisplayOrderAsc(1L))
                .thenReturn(listOf(option1, option2))

        val result = surveyOptionService.getSurveyOptionsBySurveyQuestionId(1L)

        assertEquals(2, result.size)
        assertEquals("Yes", result[0].optionText)
        assertEquals(1, result[0].displayOrder)
        assertEquals("No", result[1].optionText)
        assertEquals(2, result[1].displayOrder)

        verify(surveyQuestionRepository).findById(1L)
        verify(surveyOptionRepository).findBySurveyQuestionIdOrderByDisplayOrderAsc(1L)
    }

    @Test
    fun `get survey options by survey question id should throw when survey question not found`() {
        whenever(surveyQuestionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyQuestionNotFoundException::class.java) {
            surveyOptionService.getSurveyOptionsBySurveyQuestionId(99L)
        }

        verify(surveyQuestionRepository).findById(99L)
    }

    @Test
    fun `update survey option should return updated response`() {
        val request = UpdateSurveyOptionRequest(
                optionText = "Strongly Agree",
                optionValue = "STRONGLY_AGREE",
                displayOrder = 2
        )

        val surveyQuestion = makeSurveyQuestion(id = 1L)
        val existingOption = makeSurveyOption(
                id = 1L,
                surveyQuestion = surveyQuestion,
                optionText = "Agree",
                optionValue = "AGREE",
                displayOrder = 1
        )
        val updatedOption = existingOption.copy(
                optionText = "Strongly Agree",
                optionValue = "STRONGLY_AGREE",
                displayOrder = 2
        )

        whenever(surveyOptionRepository.findById(1L)).thenReturn(Optional.of(existingOption))
        whenever(surveyOptionRepository.findBySurveyQuestionIdOrderByDisplayOrderAsc(1L))
                .thenReturn(listOf(existingOption))
        whenever(surveyOptionRepository.save(any<SurveyOption>())).thenReturn(updatedOption)

        val result = surveyOptionService.updateSurveyOption(1L, request)

        assertEquals(1L, result.id)
        assertEquals("Strongly Agree", result.optionText)
        assertEquals("STRONGLY_AGREE", result.optionValue)
        assertEquals(2, result.displayOrder)

        verify(surveyOptionRepository).findById(1L)
        verify(surveyOptionRepository).findBySurveyQuestionIdOrderByDisplayOrderAsc(1L)
        verify(surveyOptionRepository).save(any<SurveyOption>())
    }

    @Test
    fun `update survey option should throw when option not found`() {
        val request = UpdateSurveyOptionRequest(
                optionText = "Strongly Agree",
                optionValue = "STRONGLY_AGREE",
                displayOrder = 2
        )

        whenever(surveyOptionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SurveyOptionNotFoundException::class.java) {
            surveyOptionService.updateSurveyOption(99L, request)
        }

        verify(surveyOptionRepository).findById(99L)
        verify(surveyOptionRepository, never()).save(any<SurveyOption>())
    }

    @Test
    fun `update survey option should throw when display order conflicts with another option`() {
        val request = UpdateSurveyOptionRequest(
                optionText = "Strongly Agree",
                optionValue = "STRONGLY_AGREE",
                displayOrder = 2
        )

        val surveyQuestion = makeSurveyQuestion(id = 1L)
        val existingOption = makeSurveyOption(
                id = 1L,
                surveyQuestion = surveyQuestion,
                optionText = "Agree",
                optionValue = "AGREE",
                displayOrder = 1
        )
        val conflictingOption = makeSurveyOption(
                id = 2L,
                surveyQuestion = surveyQuestion,
                optionText = "Neutral",
                optionValue = "NEUTRAL",
                displayOrder = 2
        )

        whenever(surveyOptionRepository.findById(1L)).thenReturn(Optional.of(existingOption))
        whenever(surveyOptionRepository.findBySurveyQuestionIdOrderByDisplayOrderAsc(1L))
                .thenReturn(listOf(existingOption, conflictingOption))

        assertThrows(DuplicateSurveyOptionDisplayOrderException::class.java) {
            surveyOptionService.updateSurveyOption(1L, request)
        }

        verify(surveyOptionRepository).findById(1L)
        verify(surveyOptionRepository).findBySurveyQuestionIdOrderByDisplayOrderAsc(1L)
        verify(surveyOptionRepository, never()).save(any<SurveyOption>())
    }

    @Test
    fun `delete survey option should delete successfully`() {
        val option = makeSurveyOption(id = 1L)

        whenever(surveyOptionRepository.findById(1L)).thenReturn(Optional.of(option))

        surveyOptionService.deleteSurveyOption(1L)

        verify(surveyOptionRepository).findById(1L)
        verify(surveyOptionRepository).delete(option)
    }

    @Test
    fun `delete survey option should throw when option not found`() {
        whenever(surveyOptionRepository.findById(2L)).thenReturn(Optional.empty())

        assertThrows(SurveyOptionNotFoundException::class.java) {
            surveyOptionService.deleteSurveyOption(2L)
        }

        verify(surveyOptionRepository).findById(2L)
        verify(surveyOptionRepository, never()).delete(any<SurveyOption>())
    }
}
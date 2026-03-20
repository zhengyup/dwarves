package com.huskie.dwarves.interviewer.service

import com.huskie.dwarves.interviewer.dto.CreateInterviewerRequest
import com.huskie.dwarves.interviewer.dto.UpdateInterviewerRequest
import com.huskie.dwarves.interviewer.entity.Interviewer
import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

class InterviewerServiceTest {

    private val interviewerRepository: InterviewerRepository = mock()
    private val interviewerService = InterviewerService(interviewerRepository)

    @Test
    fun `create interviewer should return saved response`() {
        val request = CreateInterviewerRequest(
                name = "John Tan",
                email = "john.tan@example.com"
        )

        val savedInterviewer = makeInterviewer(
                id = 1L,
                name = "John Tan",
                email = "john.tan@example.com"
        )

        whenever(interviewerRepository.save(any<Interviewer>())).thenReturn(savedInterviewer)

        val result = interviewerService.createInterviewer(request)

        assertEquals(1L, result.id)
        assertEquals("John Tan", result.name)
        assertEquals("john.tan@example.com", result.email)

        verify(interviewerRepository).save(any<Interviewer>())
    }

    @Test
    fun `get interviewer by id should return response`() {
        val interviewer = makeInterviewer(
                id = 1L,
                name = "John Tan",
                email = "john.tan@example.com"
        )

        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))

        val result = interviewerService.getInterviewerById(1L)

        assertEquals(1L, result.id)
        assertEquals("John Tan", result.name)
        assertEquals("john.tan@example.com", result.email)

        verify(interviewerRepository).findById(1L)
    }

    @Test
    fun `get interviewer by id should throw when interviewer not found`() {
        whenever(interviewerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<InterviewerNotFoundException> {
            interviewerService.getInterviewerById(99L)
        }

        verify(interviewerRepository).findById(99L)
    }

    @Test
    fun `get all interviewers should return all responses`() {
        val interviewer1 = makeInterviewer(
                id = 1L,
                name = "John Tan",
                email = "john.tan@example.com"
        )
        val interviewer2 = makeInterviewer(
                id = 2L,
                name = "Sarah Lim",
                email = "sarah.lim@example.com"
        )

        whenever(interviewerRepository.findAll()).thenReturn(listOf(interviewer1, interviewer2))

        val result = interviewerService.getAllInterviewers()

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("John Tan", result[0].name)
        assertEquals("john.tan@example.com", result[0].email)
        assertEquals(2L, result[1].id)
        assertEquals("Sarah Lim", result[1].name)
        assertEquals("sarah.lim@example.com", result[1].email)

        verify(interviewerRepository).findAll()
    }

    @Test
    fun `update interviewer should return updated response`() {
        val request = UpdateInterviewerRequest(
                name = "John Tan Updated",
                email = "john.updated@example.com"
        )

        val existingInterviewer = makeInterviewer(
                id = 1L,
                name = "John Tan",
                email = "john.tan@example.com"
        )

        val updatedInterviewer = existingInterviewer.copy(
                name = "John Tan Updated",
                email = "john.updated@example.com"
        )

        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(existingInterviewer))
        whenever(interviewerRepository.save(any<Interviewer>())).thenReturn(updatedInterviewer)

        val result = interviewerService.updateInterviewer(1L, request)

        assertEquals(1L, result.id)
        assertEquals("John Tan Updated", result.name)
        assertEquals("john.updated@example.com", result.email)

        verify(interviewerRepository).findById(1L)
        verify(interviewerRepository).save(any<Interviewer>())
    }

    @Test
    fun `update interviewer should throw when interviewer not found`() {
        val request = UpdateInterviewerRequest(
                name = "John Tan Updated",
                email = "john.updated@example.com"
        )

        whenever(interviewerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<InterviewerNotFoundException> {
            interviewerService.getInterviewerById(99L)
        }

        verify(interviewerRepository).findById(99L)
        verify(interviewerRepository, never()).save(any<Interviewer>())
    }

    @Test
    fun `delete interviewer should delete successfully`() {
        val interviewer = makeInterviewer(
                id = 1L,
                name = "John Tan",
                email = "john.tan@example.com"
        )

        whenever(interviewerRepository.findById(1L)).thenReturn(Optional.of(interviewer))

        interviewerService.deleteInterviewer(1L)

        verify(interviewerRepository).findById(1L)
        verify(interviewerRepository).delete(interviewer)
    }

    @Test
    fun `delete interviewer should throw when interviewer not found`() {
        whenever(interviewerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<InterviewerNotFoundException> {
            interviewerService.getInterviewerById(99L)
        }

        verify(interviewerRepository).findById(99L)
        verify(interviewerRepository, never()).delete(any<Interviewer>())
    }
}
package com.huskie.dwarves.organization.service

import com.huskie.dwarves.organization.dto.CreateOrganizationRequest
import com.huskie.dwarves.organization.dto.UpdateOrganizationRequest
import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.organization.exception.OrganizationNotFoundException
import com.huskie.dwarves.organization.repository.OrganizationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional
import org.mockito.kotlin.doReturn

class OrganizationServiceTest {

    private val organizationRepository: OrganizationRepository = mock()
    private val organizationService = OrganizationService(organizationRepository)

    @Test
    fun `create organization should return saved response`() {
        val request = CreateOrganizationRequest(
                name = "Ministry of Health",
                code = "MOH"
        )

        val savedOrganization = Organization(
                id = 1L,
                name = "Ministry of Health",
                code = "MOH",
                createdAt = LocalDateTime.now()
        )

        whenever(organizationRepository.save(any<Organization>())).thenReturn(savedOrganization)
        val result = organizationService.createOrganization(request)

        assertEquals(1L, result.id)
        assertEquals("Ministry of Health", result.name)
        assertEquals("MOH", result.code)
        verify(organizationRepository, times(1)).save(any())
    }

    @Test
    fun `get organization by id should return organization when found`() {
        val organization = Organization(
                id = 1L,
                name = "Ministry of Health",
                code = "MOH",
                createdAt = LocalDateTime.now()
        )

        whenever(organizationRepository.findById(1L)).thenReturn(Optional.of(organization))

        val result = organizationService.getOrganizationById(1L)

        assertEquals(1L, result.id)
        assertEquals("Ministry of Health", result.name)
        assertEquals("MOH", result.code)
        verify(organizationRepository, times(1)).findById(1L)
    }

    @Test
    fun `get organization by id should throw when not found`() {
        whenever(organizationRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(OrganizationNotFoundException::class.java) {
            organizationService.getOrganizationById(999L)
        }

        verify(organizationRepository, times(1)).findById(999L)
    }

    @Test
    fun `update organization should update fields and return updated response`() {
        val existing = Organization(
                id = 1L,
                name = "Old Name",
                code = "OLD",
                createdAt = LocalDateTime.now()
        )

        val request = UpdateOrganizationRequest(
                name = "New Name",
                code = "NEW"
        )

        val updated = Organization(
                id = 1L,
                name = "New Name",
                code = "NEW",
                createdAt = existing.createdAt
        )

        whenever(organizationRepository.findById(1L)).thenReturn(Optional.of(existing))
        whenever(organizationRepository.save(any<Organization>())).thenReturn(updated)

        val result = organizationService.updateOrganization(1L, request)

        assertEquals(1L, result.id)
        assertEquals("New Name", result.name)
        assertEquals("NEW", result.code)
        verify(organizationRepository, times(1)).findById(1L)
        verify(organizationRepository, times(1)).save(any())
    }

    @Test
    fun `delete organization should delete existing organization`() {
        val existing = Organization(
                id = 1L,
                name = "Ministry of Health",
                code = "MOH",
                createdAt = LocalDateTime.now()
        )

        whenever(organizationRepository.findById(1L)).thenReturn(Optional.of(existing))

        organizationService.deleteOrganization(1L)

        verify(organizationRepository, times(1)).findById(1L)
        verify(organizationRepository, times(1)).delete(existing)
    }

    @Test
    fun `delete organization should throw when organization not found`() {
        whenever(organizationRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(OrganizationNotFoundException::class.java) {
            organizationService.deleteOrganization(999L)
        }

        verify(organizationRepository, times(1)).findById(999L)
        verify(organizationRepository, never()).delete(any())
    }

    @Test
    fun `get all organizations should return list of organization responses`() {
        val organizations = listOf(
                Organization(
                        id = 1L,
                        name = "Ministry of Health",
                        code = "MOH",
                        createdAt = LocalDateTime.now()
                ),
                Organization(
                        id = 2L,
                        name = "Ministry of Education",
                        code = "MOE",
                        createdAt = LocalDateTime.now()
                )
        )

        whenever(organizationRepository.findAll()).thenReturn(organizations)

        val result = organizationService.getAllOrganizations()

        assertEquals(2, result.size)
        assertEquals("MOH", result[0].code)
        assertEquals("MOE", result[1].code)
        verify(organizationRepository, times(1)).findAll()
    }
}
package com.huskie.dwarves.organization.service

import com.huskie.dwarves.organization.dto.CreateOrganizationRequest
import com.huskie.dwarves.organization.dto.OrganizationResponse
import com.huskie.dwarves.organization.dto.UpdateOrganizationRequest
import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.organization.exceptions.OrganizationNotFoundException
import com.huskie.dwarves.organization.repository.OrganizationRepository
import org.springframework.stereotype.Service

@Service
class OrganizationService(
        private val organizationRepository: OrganizationRepository
) {

    fun createOrganization(request: CreateOrganizationRequest): OrganizationResponse {
        val organization = Organization(
                name = request.name,
                code = request.code
        )

        val saved = organizationRepository.save(organization)
        return saved.toResponse()
    }

    fun getAllOrganizations(): List<OrganizationResponse> {
        return organizationRepository.findAll().map { it.toResponse() }
    }

    fun getOrganizationById(id: Long): OrganizationResponse {
        val organization = organizationRepository.findById(id)
                .orElseThrow { OrganizationNotFoundException(id) }

        return organization.toResponse()
    }

    fun updateOrganization(id: Long, request: UpdateOrganizationRequest): OrganizationResponse {
        val existing = organizationRepository.findById(id)
                .orElseThrow { OrganizationNotFoundException(id) }

        val updated = Organization(
                id = existing.id,
                name = request.name,
                code = request.code,
                createdAt = existing.createdAt
        )

        val saved = organizationRepository.save(updated)
        return saved.toResponse()
    }

    fun deleteOrganization(id: Long) {
        val existing = organizationRepository.findById(id)
                .orElseThrow { OrganizationNotFoundException(id) }

        organizationRepository.delete(existing)
    }

    private fun Organization.toResponse(): OrganizationResponse {
        return OrganizationResponse(
                id = requireNotNull(this.id),
                name = this.name,
                code = this.code,
                createdAt = this.createdAt
        )
    }
}
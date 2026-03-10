package com.huskie.dwarves.organization.controller

import com.huskie.dwarves.organization.dto.CreateOrganizationRequest
import com.huskie.dwarves.organization.dto.OrganizationResponse
import com.huskie.dwarves.organization.dto.UpdateOrganizationRequest
import com.huskie.dwarves.organization.service.OrganizationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/organization")
class OrganizationController(
        private val organizationService: OrganizationService
) {

    @PostMapping
    fun createOrganization(
            @Valid @RequestBody request: CreateOrganizationRequest
    ): OrganizationResponse {
        return organizationService.createOrganization(request)
    }

    @GetMapping
    fun getAllOrganizations(): List<OrganizationResponse> {
        return organizationService.getAllOrganizations()
    }

    @GetMapping("/{id}")
    fun getOrganizationById(@PathVariable id: Long): OrganizationResponse {
        return organizationService.getOrganizationById(id)
    }

    @PutMapping("/{id}")
    fun updateOrganization(
            @PathVariable id: Long,
            @Valid @RequestBody request: UpdateOrganizationRequest
    ): OrganizationResponse {
        return organizationService.updateOrganization(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteOrganization(@PathVariable id: Long) {
        organizationService.deleteOrganization(id)
    }
}
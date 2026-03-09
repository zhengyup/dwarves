package com.huskie.dwarves.organization.repository

import com.huskie.dwarves.organization.entity.Organization
import org.springframework.data.jpa.repository.JpaRepository

interface OrganizationRepository : JpaRepository<Organization, Long>

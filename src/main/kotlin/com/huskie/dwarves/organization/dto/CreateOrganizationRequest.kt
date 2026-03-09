package com.huskie.dwarves.organization.dto

import jakarta.validation.constraints.NotBlank

class CreateOrganizationRequest(
        @field:NotBlank
        val name: String,

        @field:NotBlank
        val code: String

)
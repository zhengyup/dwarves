package com.huskie.dwarves.interviewer.dto

data class UpdateInterviewerRequest(
        val name: String,
        val email: String? = null
)
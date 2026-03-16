package com.huskie.dwarves.interviewer.dto

data class CreateInterviewerRequest(
        val name: String,
        val email: String? = null
)
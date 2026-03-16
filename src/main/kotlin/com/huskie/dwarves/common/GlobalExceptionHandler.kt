package com.huskie.dwarves.common

import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.surveyoption.exceptions.*
import com.huskie.dwarves.surveyquestion.exceptions.*
import com.huskie.dwarves.survey.exceptions.*
import com.huskie.dwarves.organization.exceptions.*

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(
        val message: String
)

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OrganizationNotFoundException::class)
    fun handleSurveyQuestionNotFound(ex: OrganizationNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(ex.message ?: "Organization question not found"))
    }
    @ExceptionHandler(SurveyNotFoundException::class)
    fun handleSurveyQuestionNotFound(ex: SurveyNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(ex.message ?: "Survey not found"))
    }
    @ExceptionHandler(SurveyOptionNotFoundException::class)
    fun handleSurveyOptionNotFound(ex: SurveyOptionNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(ex.message ?: "Survey option not found"))
    }

    @ExceptionHandler(SurveyQuestionNotFoundException::class)
    fun handleSurveyQuestionNotFound(ex: SurveyQuestionNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(ex.message ?: "Survey question not found"))
    }

    @ExceptionHandler(DuplicateSurveyOptionDisplayOrderException::class)
    fun handleDuplicateDisplayOrder(ex: DuplicateSurveyOptionDisplayOrderException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse(ex.message ?: "Duplicate display order"))
    }

    @ExceptionHandler(InterviewerNotFoundException::class)
    fun handleInterviewerNotFound(ex: InterviewerNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(ex.message ?: "Interviewer not found"))
    }
}
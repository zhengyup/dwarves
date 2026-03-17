package com.huskie.dwarves.answer.repository

import com.huskie.dwarves.answer.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository : JpaRepository<Answer, Long> {
    fun findBySubmissionId(submissionId: Long): List<Answer>
    fun findBySurveyQuestionId(surveyQuestionId: Long): List<Answer>
}
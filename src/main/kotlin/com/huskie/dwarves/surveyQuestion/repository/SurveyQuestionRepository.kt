package com.huskie.dwarves.surveyQuestion.repository

import com.huskie.dwarves.surveyQuestion.entity.SurveyQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface SurveyQuestionRepository : JpaRepository<SurveyQuestion, Long> {
    fun findBySurveyIdOrderByDisplayOrderAsc(surveyId: Long): List<SurveyQuestion>
}



package com.huskie.dwarves.surveyquestion.repository

import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface SurveyQuestionRepository : JpaRepository<SurveyQuestion, Long> {
    fun findBySurveyIdOrderByDisplayOrderAsc(surveyId: Long): List<SurveyQuestion>
    fun existsBySurveyIdAndDisplayOrder(surveyQuestionId: Long, displayOrder: Int): Boolean
}



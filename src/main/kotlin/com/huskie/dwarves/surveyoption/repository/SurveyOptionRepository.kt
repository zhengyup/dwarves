package com.huskie.dwarves.surveyoption.repository

import com.huskie.dwarves.surveyoption.entity.SurveyOption
import org.springframework.data.jpa.repository.JpaRepository

interface SurveyOptionRepository : JpaRepository<SurveyOption, Long> {
    fun findBySurveyQuestionIdOrderByDisplayOrderAsc(surveyQuestionId: Long): List<SurveyOption>
    fun existsBySurveyQuestionIdAndDisplayOrder(surveyQuestionId: Long, displayOrder: Int): Boolean
}
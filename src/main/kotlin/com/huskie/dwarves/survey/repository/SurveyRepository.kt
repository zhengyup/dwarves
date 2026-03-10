package com.huskie.dwarves.survey.repository

import com.huskie.dwarves.survey.entity.Survey
import org.springframework.data.jpa.repository.JpaRepository

interface SurveyRepository : JpaRepository<Survey, Long>
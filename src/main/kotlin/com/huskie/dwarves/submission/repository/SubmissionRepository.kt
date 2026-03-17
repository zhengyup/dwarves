package com.huskie.dwarves.submission.repository

import com.huskie.dwarves.submission.entity.Submission
import org.springframework.data.jpa.repository.JpaRepository

interface SubmissionRepository : JpaRepository<Submission, Long> {
    fun findByInterviewerId(interviewerId: Long): List<Submission>
    fun findBySurveyId(surveyId: Long): List<Submission>
}
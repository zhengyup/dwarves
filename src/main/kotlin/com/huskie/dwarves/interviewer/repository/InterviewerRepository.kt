package com.huskie.dwarves.interviewer.repository

import com.huskie.dwarves.interviewer.entity.Interviewer
import org.springframework.data.jpa.repository.JpaRepository

interface InterviewerRepository : JpaRepository<Interviewer, Long>
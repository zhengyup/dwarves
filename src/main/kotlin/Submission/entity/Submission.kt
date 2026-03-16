package com.huskie.dwarves.submission.entity

import com.huskie.dwarves.interviewer.entity.Interviewer
import com.huskie.dwarves.survey.entity.Survey
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "submission")
data class Submission(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "interviewer_id", nullable = false)
        val interviewer: Interviewer,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "survey_id", nullable = false)
        val survey: Survey,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
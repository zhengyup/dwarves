package com.huskie.dwarves.surveyquestion.entity

import com.huskie.dwarves.survey.entity.Survey
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "survey_question")
class SurveyQuestion (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "survey_id", nullable = false)
        val survey: Survey,

        @Column(nullable = false)
        val questionText: String,

        @Column(nullable = false)
        val questionType: String,

        @Column(nullable = false)
        val isRequired: Boolean,

        @Column(nullable = false)
        val displayOrder: Int,

        @Column(name= "created_at", nullable = false)
        val createdAt : LocalDateTime = LocalDateTime.now()
)
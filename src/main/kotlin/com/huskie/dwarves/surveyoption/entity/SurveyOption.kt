package com.huskie.dwarves.surveyoption.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
        name = "survey_option",
        uniqueConstraints = [
            UniqueConstraint(
                    name = "uq_survey_option_display_order",
                    columnNames = ["survey_question_id", "display_order"]
            )
        ]
)
data class SurveyOption(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "survey_question_id", nullable = false)
        val surveyQuestionId: Long,

        @Column(name = "option_text", nullable = false)
        val optionText: String,

        @Column(name = "option_value")
        val optionValue: String? = null,

        @Column(name = "display_order", nullable = false)
        val displayOrder: Int,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
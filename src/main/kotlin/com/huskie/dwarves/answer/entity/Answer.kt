package com.huskie.dwarves.answer.entity

import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.surveyoption.entity.SurveyOption
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "answer")
data class Answer(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "submission_id", nullable = false)
        val submission: Submission,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "survey_question_id", nullable = false)
        val surveyQuestion: SurveyQuestion,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "survey_option_id")
        val surveyOption: SurveyOption? = null,

        @Column(name = "answer_text")
        val answerText: String? = null,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
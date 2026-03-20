package com.huskie.dwarves.util

import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.interviewer.entity.Interviewer
import com.huskie.dwarves.organization.entity.Organization
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.survey.entity.Survey
import com.huskie.dwarves.surveyoption.entity.SurveyOption
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import java.time.LocalDateTime
    fun makeOrganization(
            id: Long? = 1L,
            name: String = "Ministry of Health",
            code: String = "MOH"
    ) = Organization(
            id = id,
            name = name,
            code = code,
            createdAt = LocalDateTime.now()
    )

    fun makeSurvey(
            id: Long? = 1L,
            organization: Organization = makeOrganization(),
            name: String = "Health Survey",
            description: String = "Survey description"
    ) = Survey(
            id = id,
            organization = organization,
            name = name,
            description = description,
            createdAt = LocalDateTime.now()
    )

    fun makeInterviewer(
            id: Long? = 1L,
            name: String = "John Tan",
            email: String? = "john.tan@example.com"
    ) = Interviewer(
            id = id,
            name = name,
            email = email,
            createdAt = LocalDateTime.now()
    )

    fun makeSubmission(
            id: Long? = 10L,
            interviewer: Interviewer = makeInterviewer(),
            survey: Survey = makeSurvey()
    ) = Submission(
            id = id,
            interviewer = interviewer,
            survey = survey,
            createdAt = LocalDateTime.now()
    )

    fun makeSurveyQuestion(
            id: Long = 1L,
            survey: Survey = makeSurvey(),
            questionText: String = "Do you exercise regularly?",
            questionType: String = "MULTIPLE_CHOICE",
            isRequired: Boolean = true,
            displayOrder: Int = 1
    ) = SurveyQuestion(
            id = id,
            survey = survey,
            questionText = questionText,
            questionType = questionType,
            isRequired = isRequired,
            displayOrder = displayOrder,
            createdAt = LocalDateTime.now()
    )

    fun makeSurveyOption(
            id: Long = 1L,
            surveyQuestion: SurveyQuestion = makeSurveyQuestion(),
            optionText: String = "Yes",
            optionValue: String? = "YES",
            displayOrder: Int = 1
    ) = SurveyOption(
            id = id,
            surveyQuestion = surveyQuestion,
            optionText = optionText,
            optionValue = optionValue,
            displayOrder = displayOrder,
            createdAt = LocalDateTime.now()
    )

    fun makeAnswer(
            id: Long? = 1L,
            submission: Submission = makeSubmission(),
            surveyQuestion: SurveyQuestion = makeSurveyQuestion(),
            surveyOption: SurveyOption? = null,
            answerText: String? = null
    ) = Answer(
            id = id,
            submission = submission,
            surveyQuestion = surveyQuestion,
            surveyOption = surveyOption,
            answerText = answerText,
            createdAt = LocalDateTime.now()
    )


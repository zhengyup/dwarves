package com.huskie.dwarves.submission.service

import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.submission.dto.SubmitAnswerRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyResponse
import com.huskie.dwarves.submission.exceptions.MissingRequiredAnswerException
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.springframework.stereotype.Service

@Service
class SubmissionWorkflowService (
        private val surveyRepository: SurveyRepository,
        private val questionRepository: SurveyQuestionRepository,
) {

    fun submitSurvey(request: SubmitSurveyRequest) : SubmitSurveyResponse {
        // survey exists
        val survey = surveyRepository.findById(request.surveyId)
                .orElseThrow{SurveyNotFoundException(request.surveyId)}

    // given are valid in terms of -no overlapping questionId, questions with isRequired have answers,
        val questions = questionRepository.findBySurveyIdOrderByDisplayOrderAsc(request.surveyId)

        if (request.answers.size != request.answers.distinctBy { it.questionId }.size) {
            throw IllegalArgumentException("Duplicate questionId detected")
        }

        validateAllRequiredQuestionAnswered(questions, request.answers)


    // answers are valid for each given questionType -> open ended have text, mcq has multiple valid options, multiple response has at least one valid option selected
        //transactionally insert all answers and the submission and return the SubmitSurveyResponse


    }
    fun validateAllRequiredQuestionAnswered(questions:List<SurveyQuestion>,
                                            answers:List<SubmitAnswerRequest>) {
        val answeredIds = HashSet<Long>()
        answers.forEach{ item -> answeredIds.add(item.questionId) }
        questions.forEach{
            item ->
                val questionId = item.id ?: throw IllegalStateException("Survey question id cannot be null")
                if (item.isRequired && !answeredIds.contains(item.id)) {
                    throw MissingRequiredAnswerException(questionId)
                }
        }
    }
}
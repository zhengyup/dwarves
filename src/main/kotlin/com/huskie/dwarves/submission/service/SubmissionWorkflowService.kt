package com.huskie.dwarves.submission.service

import com.huskie.dwarves.answer.dto.AnswerResponse
import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.interviewer.exception.InterviewerNotFoundException
import com.huskie.dwarves.interviewer.repository.InterviewerRepository
import com.huskie.dwarves.submission.dto.SubmissionResponse
import com.huskie.dwarves.submission.dto.SubmitAnswerRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyRequest
import com.huskie.dwarves.submission.dto.SubmitSurveyResponse
import com.huskie.dwarves.submission.entity.Submission
import com.huskie.dwarves.submission.exceptions.MissingRequiredAnswerException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.survey.exceptions.SurveyNotFoundException
import com.huskie.dwarves.survey.repository.SurveyRepository
import com.huskie.dwarves.surveyoption.entity.SurveyOption
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.entity.SurveyQuestion
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmissionWorkflowService (

        private val answerRepository: AnswerRepository,
        private val interviewerRepository: InterviewerRepository,
        private val questionRepository: SurveyQuestionRepository,
        private val surveyOptionRepository: SurveyOptionRepository,
        private val surveyRepository: SurveyRepository,
        private val submissionRepository: SubmissionRepository,


) {
    @Transactional
    fun submitSurvey(request: SubmitSurveyRequest) : SubmitSurveyResponse {
        val survey = surveyRepository.findById(request.surveyId)
                .orElseThrow{SurveyNotFoundException(request.surveyId)}

        val interviewer = interviewerRepository.findById(request.interviewerId)
                .orElseThrow{InterviewerNotFoundException(request.interviewerId)}

    // given are valid in terms of -no overlapping questionId, questions with isRequired have answers,
        val questions = questionRepository.findBySurveyIdOrderByDisplayOrderAsc(request.surveyId)

        if (request.answers.size != request.answers.distinctBy { it.questionId }.size) {
            throw IllegalArgumentException("Duplicate questionId detected")
        }

        validateAllRequiredQuestionAnswered(questions, request.answers)

        val submission = Submission(
                interviewer = interviewer,
                survey = survey,
        )
        val savedSubmission = submissionRepository.save(submission)
        val submissionId = savedSubmission.id ?: throw IllegalStateException("Submission ID was not generated")
        val answers = request.answers.map{ it -> it.toAnswer(savedSubmission)}
        val savedAnswers = answerRepository.saveAll(answers)
        return SubmitSurveyResponse(
                submissionId = submissionId,
                interviewerId = request.interviewerId,
                surveyId = request.surveyId,
                answerResponses = savedAnswers.map{ it -> it.toResponse()},
        )

    }
    private fun validateAllRequiredQuestionAnswered(questions:List<SurveyQuestion>,
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

    private fun SubmitAnswerRequest.toAnswer(submission : Submission) : Answer {
        val question = questionRepository.findById(questionId)
                .orElseThrow{SurveyQuestionNotFoundException(questionId)}
        val option: SurveyOption? = this.selectedOptionId?.let { optionId ->
            surveyOptionRepository.findById(optionId)
                    .orElseThrow { SurveyOptionNotFoundException(optionId) }
        }
        return Answer(
                submission = submission,
                surveyQuestion = question,
                surveyOption = option,
                answerText = answerText,
        )
    }

    private fun Answer.toResponse(): AnswerResponse {
        print(this)
        val answerId = this.id
                ?: throw IllegalStateException("Answer id should not be null")

        val submissionId = this.submission.id
                ?: throw IllegalStateException("Submission id should not be null")

        val surveyQuestionId = this.surveyQuestion.id
                ?: throw IllegalStateException("Survey question id should not be null")

        val surveyOptionId = this.surveyOption?.id

        return AnswerResponse(
                id = answerId,
                submissionId = submissionId,
                surveyQuestionId = surveyQuestionId,
                surveyOptionId = surveyOptionId,
                answerText = this.answerText,
                createdAt = this.createdAt
        )
    }
}
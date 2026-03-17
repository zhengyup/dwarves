package com.huskie.dwarves.answer.service

import com.huskie.dwarves.answer.dto.AnswerResponse
import com.huskie.dwarves.answer.dto.CreateAnswerRequest
import com.huskie.dwarves.answer.dto.UpdateAnswerRequest
import com.huskie.dwarves.answer.entity.Answer
import com.huskie.dwarves.answer.exceptions.AnswerNotFoundException
import com.huskie.dwarves.answer.repository.AnswerRepository
import com.huskie.dwarves.submission.exception.SubmissionNotFoundException
import com.huskie.dwarves.submission.repository.SubmissionRepository
import com.huskie.dwarves.surveyoption.exceptions.SurveyOptionNotFoundException
import com.huskie.dwarves.surveyoption.repository.SurveyOptionRepository
import com.huskie.dwarves.surveyquestion.exceptions.SurveyQuestionNotFoundException
import com.huskie.dwarves.surveyquestion.repository.SurveyQuestionRepository
import org.springframework.stereotype.Service

@Service
class AnswerService(
        private val answerRepository: AnswerRepository,
        private val submissionRepository: SubmissionRepository,
        private val surveyQuestionRepository: SurveyQuestionRepository,
        private val surveyOptionRepository: SurveyOptionRepository
) {
    fun createAnswer(request: CreateAnswerRequest): AnswerResponse {
        val submission = submissionRepository.findById(request.submissionId)
                .orElseThrow { SubmissionNotFoundException(request.submissionId) }

        val surveyQuestion = surveyQuestionRepository.findById(request.surveyQuestionId)
                .orElseThrow { SurveyQuestionNotFoundException(request.surveyQuestionId) }

        val surveyOption = request.surveyOptionId?.let {
            surveyOptionRepository.findById(it)
                    .orElseThrow { SurveyOptionNotFoundException(it) }
        }

        val answer = Answer(
                submission = submission,
                surveyQuestion = surveyQuestion,
                surveyOption = surveyOption,
                answerText = request.answerText
        )

        return answerRepository.save(answer).toResponse()
    }

    fun getAnswerById(id: Long): AnswerResponse {
        val answer = answerRepository.findById(id)
                .orElseThrow { AnswerNotFoundException(id) }

        return answer.toResponse()
    }

    fun getAllAnswers(): List<AnswerResponse> {
        return answerRepository.findAll().map { it.toResponse() }
    }

    fun getAnswersBySubmissionId(submissionId: Long): List<AnswerResponse> {
        return answerRepository.findBySubmissionId(submissionId)
                .map { it.toResponse() }
    }

    fun getAnswersBySurveyQuestionId(surveyQuestionId: Long): List<AnswerResponse> {
        return answerRepository.findBySurveyQuestionId(surveyQuestionId)
                .map { it.toResponse() }
    }

    fun updateAnswer(id: Long, request: UpdateAnswerRequest): AnswerResponse {
        val existingAnswer = answerRepository.findById(id)
                .orElseThrow { AnswerNotFoundException(id) }

        val submission = submissionRepository.findById(request.submissionId)
                .orElseThrow { SubmissionNotFoundException(request.submissionId) }

        val surveyQuestion = surveyQuestionRepository.findById(request.surveyQuestionId)
                .orElseThrow { SurveyQuestionNotFoundException(request.surveyQuestionId) }

        val surveyOption = request.surveyOptionId?.let {
            surveyOptionRepository.findById(it)
                    .orElseThrow { SurveyOptionNotFoundException(it) }
        }

        val updatedAnswer = existingAnswer.copy(
                submission = submission,
                surveyQuestion = surveyQuestion,
                surveyOption = surveyOption,
                answerText = request.answerText
        )

        return answerRepository.save(updatedAnswer).toResponse()
    }

    fun deleteAnswer(id: Long) {
        val answer = answerRepository.findById(id)
                .orElseThrow { AnswerNotFoundException(id) }

        answerRepository.delete(answer)
    }

    private fun Answer.toResponse(): AnswerResponse {
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
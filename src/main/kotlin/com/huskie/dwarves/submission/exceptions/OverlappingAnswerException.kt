package com.huskie.dwarves.submission.exceptions

data class OverlappingAnswerException(val questionId : Long):RuntimeException("Multiple Answers submitted " +
        "for question $questionId")

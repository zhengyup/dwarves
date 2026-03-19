package com.huskie.dwarves.submission.exceptions

data class MissingRequiredAnswerException(val id : Long) : RuntimeException("Required Question with id $id " +
        "has not been answered")

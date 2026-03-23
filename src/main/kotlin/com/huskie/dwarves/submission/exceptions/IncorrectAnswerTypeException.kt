package com.huskie.dwarves.submission.exceptions

data class IncorrectAnswerTypeException(val id : Long, val type : String) : RuntimeException("Incorrect " +
        "type given for question $id. Please provide a $type as the answer")
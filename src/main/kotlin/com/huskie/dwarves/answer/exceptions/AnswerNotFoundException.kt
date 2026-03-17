package com.huskie.dwarves.answer.exceptions

class AnswerNotFoundException(id: Long) :
        RuntimeException("Answer with id $id not found")
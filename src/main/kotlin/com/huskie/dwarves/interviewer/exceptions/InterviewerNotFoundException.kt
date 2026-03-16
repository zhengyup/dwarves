package com.huskie.dwarves.interviewer.exception

class InterviewerNotFoundException(id: Long) :
        RuntimeException("Interviewer with id $id not found")
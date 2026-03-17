package com.huskie.dwarves.submission.exception

class SubmissionNotFoundException(id: Long) :
        RuntimeException("Submission with id $id not found")
create table answer (
    id bigserial primary key,
    submission_id bigint not null,
    survey_question_id bigint not null,
    survey_option_id bigint,
    answer_text text,
    created_at timestamp not null default now(),

    constraint fk_answer_submission
        foreign key (submission_id)
        references submission(id)
        on delete cascade,

    constraint fk_answer_survey_question
        foreign key (survey_question_id)
        references survey_question(id),

    constraint fk_answer_survey_option
        foreign key (survey_option_id)
        references survey_option(id)
);

create index idx_answer_submission_id
    on answer(submission_id);

create index idx_answer_survey_question_id
    on answer(survey_question_id);

create index idx_answer_survey_option_id
    on answer(survey_option_id);
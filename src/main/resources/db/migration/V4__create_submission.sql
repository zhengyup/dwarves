create table submission (
    id bigserial primary key,
    interviewer_id bigint not null,
    survey_id bigint not null,
    created_at timestamp not null default now(),

    constraint fk_submission_interviewer
        foreign key (interviewer_id)
        references interviewer(id),

    constraint fk_submission_survey
        foreign key (survey_id)
        references survey(id)
);

create index idx_submission_interviewer_id
    on submission(interviewer_id);

create index idx_submission_survey_id
    on submission(survey_id);
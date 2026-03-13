create table survey_option (
    id bigserial primary key,
    survey_question_id bigint not null,
    option_text text not null,
    option_value varchar(255),
    display_order integer not null,
    created_at timestamp not null default now(),

    constraint fk_survey_option_question
        foreign key (survey_question_id)
        references survey_question(id)
        on delete cascade,

    constraint uq_survey_option_display_order
        unique (survey_question_id, display_order)
);

create index idx_survey_option_question_id
    on survey_option(survey_question_id);

create index idx_survey_option_question_id_display_order
    on survey_option(survey_question_id, display_order);
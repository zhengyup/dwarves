create table organization (
    id bigserial primary key,
    name varchar(255) not null,
    code varchar(100) not null unique,
    created_at timestamp not null default now()
);

create table if not exists survey (
    id bigserial PRIMARY KEY,
    organization_id bigserial NOT NULL,
    name varchar(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (organization_id) REFERENCES organization(id)
);

create table survey_question (
    id bigserial primary key,
    survey_id bigint not null,
    question_text text not null,
    question_type varchar(50) not null,
    is_required boolean not null default false,
    display_order integer not null,
    created_at timestamp not null default now(),

    constraint fk_survey_question_survey
        foreign key (survey_id) references survey(id) on delete cascade,

    constraint uq_survey_question_display_order
        unique (survey_id, display_order)
);

create index idx_survey_question_survey_id
    on survey_question(survey_id);

create index idx_survey_question_survey_id_display_order
    on survey_question(survey_id, display_order);
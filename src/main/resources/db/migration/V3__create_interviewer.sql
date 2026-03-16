create table interviewer (
    id bigserial primary key,
    name varchar(255) not null,
    email varchar(255),
    created_at timestamp not null default now()
);
create table if not exists survey (
    id bigserial primary key,
    name varchar(255) not null,
    description text,
    created_at timestamp not null default now()
);
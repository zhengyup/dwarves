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
-- liquibase formatted sql

-- changeset vasilydemin:1
CREATE TABLE chat_config(
    id bigserial primary key,
    chat_id bigint,
    chat_state bigint
);

-- changeset vasilydemin:2
CREATE TABLE users(
    id bigserial primary key,
    first_name text,
    second_name text,
    nick_name text,
    address text,
    mobile_phone text,
    chat_id bigint
);

CREATE TABLE animals(
    id bigserial primary key,
    "name" text,
    kind text,
    breed text,
    color text,
    features text,
    file_path text,
    file_size bigint,
    avatar_picture bytea,
    owner_id bigint references users (id),
    adoption_date timestamp
);

CREATE TABLE daily_reports(
    id bigserial primary key,
    date_time timestamp,
    file_path text,
    file_size integer,
    small_picture bytea,
    diet text,
    general_well_being text,
    change_in_behavior text,
    animal_id bigint references animals (id)
);

-- changeset vasilydemin:3
ALTER TABLE animals ADD COLUMN media_type text;

-- liquibase formatted sql

-- changeset vasilydemin:1
CREATE TABLE chat_config(
    chat_config_id serial primary key,
    chat_id integer,
    chat_state integer
);

-- changeset vasilydemin:2
CREATE TABLE users(
    user_id serial primary key,
    first_name text,
    second_name text,
    nick_name text,
    address text,
    mobile_phone text,
    chat_id integer
);

CREATE TABLE animals(
    animal_id serial primary key,
    "name" text,
    kind text,
    breed text,
    color text,
    features text,
    file_path text,
    file_size integer,
    avatar_picture bytea,
    "owner" integer references users (user_id),
    adoption_date timestamp
);

CREATE TABLE daily_reports(
    daily_report_id serial primary key,
    datetime timestamp,
    file_path text,
    file_size integer,
    small_picture bytea,
    diet text,
    general_well_being text,
    change_in_behavior text,
    animal integer references animals (animal_id)
);
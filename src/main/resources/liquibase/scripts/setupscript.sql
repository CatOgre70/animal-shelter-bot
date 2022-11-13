-- liquibase formatted sql

-- changeset vasilydemin:1
CREATE TABLE bot_config(
    id serial primary key,
    bot_name text,
    access_token text,
    telegram_callback_answer_temp text
);

CREATE TABLE chat_config(
    id serial primary key,
    chat_id integer,
    chat_state integer
);

CREATE TABLE users(
    id serial primary key,
    first_name text,
    second_name text,
    nick_name text,
    address text,
    mobile_phone text,
    chat_id integer
);

CREATE TABLE animals(
    id serial primary key,
    "name" text,
    kind text,
    breed text,
    color text,
    features text,
    file_path text,
    file_size integer,
    avatar_picture bytea,
    "owner" integer references users (id),
    adoption_date timestamp
);

CREATE TABLE daily_reports(
    id serial primary key,
    datetime timestamp,
    file_path text,
    file_size integer,
    small_picture bytea,
    diet text,
    general_well_being text,
    change_in_behavior text,
    animal integer references animals (id)
);
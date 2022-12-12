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
    avatar_preview bytea,
    owner_id bigint references users (id),
    adoption_date timestamp
);

CREATE TABLE daily_reports(
    id bigserial primary key,
    date_time timestamp,
    file_path text,
    file_size bigint,
    small_picture bytea,
    diet text,
    general_well_being text,
    change_in_behavior text,
    animal_id bigint references animals (id)
);

-- changeset vasilydemin:3
ALTER TABLE animals ADD COLUMN media_type text;

-- changeset vasilydemin:4
INSERT INTO users (first_name, second_name, nick_name)
VALUES ('Vasily', 'Demin', 'CatOgre'),
       ('Alexander', 'Petrov', 'Sashka'),
       ('Sergey', 'Pushkin', 'Pushok'),
       ('Ivan', 'Tolstoi', 'Tolstyi'),
       ('Petr', 'Ivanov', 'Ivan');
INSERT INTO animals ("name", kind, breed, color, features)
VALUES ('Matroskin', 'Cat', 'Maine Coon', 'Gray', 'Professional killer'),
       ('Sharik', 'Dog', 'Cur', 'Brown', 'loves rotten herring from the dump, but is weak in the stomach, so never give it!'),
       ('Burenka', 'Cow', 'Yaroslavl', 'Brown with white', 'Must be milked twice a day'),
       ('Bobik', 'Dog', 'Cur', 'Black', 'Runs away sometimes'),
       ('Musia', 'Cat', 'Siberian', 'Black with white tie', 'Do not let to Matroskin!');

-- changeset vasilydemin:5
UPDATE users SET chat_id = 334390754 WHERE first_name = 'Vasily' AND second_name = 'Demin';

-- changeset vasilydemin:6
INSERT INTO chat_config (chat_id, chat_state)
VALUES (334390754, 0);

-- changeset vasilydemin:7
ALTER TABLE daily_reports ADD COLUMN media_type text;

-- changeset vasilydemin:8
UPDATE animals SET owner_id = 1 WHERE name = 'Sharik';
UPDATE animals SET adoption_date = timestamp '2022-12-09 9:00:00.000' WHERE name = 'Sharik';
INSERT INTO daily_reports (date_time, diet, general_well_being, change_in_behavior, animal_id)
VALUES (timestamp '2022-12-12 14:15:00.000', 'Оптяь жрал тухлую селедку на помойке', 'Потом блевал, но выглядел довольным',
        'Надо приучить его не есть с помойки, а также не убегать от меня во время прогулки', 2),
       (timestamp '2022-12-11 15:45:00.000', 'Жрал тухлую селедку на помойке', 'Потом блевал, но выглядел страшно довольным',
        'Никак не приучу его не есть с помойки, а также не убегать от меня во время прогулки', 2),
       (timestamp '2022-12-10 19:00:00.000', 'Ел корм, купленный в зоомагазине по рекомендациям лучших собаководов',
        'Спал, гулял нормально, выглядит хорошо', 'Попробую отпусть его завтра с поводка', 2),
       (timestamp '2022-12-09 12:00:00.000', 'Ничего не ел, только пил, по-видимому привыкает к новому месту',
        'Спал плохо, почти не отходил от моей кровати', 'Завтра пойдем гулять во двор', 2);

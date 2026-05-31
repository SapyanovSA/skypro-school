--Создание таблицы и колонок машины
create table car
(
    id    serial primary key,
    brand varchar(100),
    model varchar(100),
    cost  NUMERIC
);

--Создание таблицы и колонок человека
create table human
(
    id         serial primary key,
    name       varchar(100),
    age        integer,
    has_rights boolean,
    car_id     INTEGER REFERENCES car (id)
);
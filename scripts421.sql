-- Проверка на возраст
alter table student
    add constraint age_constraint check (age >= 16);

--Проверка на пустое значение и иникальность
alter table student
    alter column name set not null;

alter table student
    add constraint name_unique unique (name);

-- Уникальность названия класса и цвета класса
alter table faculty
    add constraint name_color_unique unique (name, color);

--Значение по умолчанию
alter table student
    alter column age set default 20;
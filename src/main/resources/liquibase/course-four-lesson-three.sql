-- liquibase formatted sql

-- changeset ssapyanov:1
CREATE INDEX faculty_name_color_index ON faculty(name, color);

CREATE INDEX student_getName_index ON student(name);

-- changeset ssapyanov:2
CREATE INDEX faculty_getByNameAndColor_index ON faculty (name, color);


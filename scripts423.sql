--Получить имя, восраст вместе с названием факультета
select student.name, student.age, faculty.name
from student
         inner join faculty on student.faculty_id = faculty.id;

--Получить студентов у которых должны быть аватарки
select student.name, student.age
from student
         INNER JOIN avatar ON student.id = avatar.student_id;
create user student password 'chocolatefrog';
create database hogwarts owner student;
grant all privileges on database hogwarts to student;

select * from student;

select * from student
where age between 10 and 20;

select name from student;

select * from student
where name ilike '%о%';

select * from student
where age < id;

select * from student
order by age;
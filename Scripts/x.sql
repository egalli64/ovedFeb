--
-- Sequence Table Generator

create table x1_employees(
    id integer primary key
);

create table x_table_generator(
    sequence_name varchar(20),
    next_val integer
);

-- drop table x1_employees;
-- drop table x_table_generator;

--
-- OneToOne

create table x2_workstations(
    wst_id integer primary key
);

-- drop table x2_workstations;
-- select * from x2_workstations;

create table x2_employees(
    emp_id integer primary key,
    wst_id integer referencing x2_workstations(wst_id) unique
);

-- drop table x2_employees;

--
-- ManyToOne

create table x3_departments(
    dep_id integer primary key
);

create table x3_employees(
    emp_id integer primary key,
    dep_id integer referencing x3_departments(dep_id)
);

--
-- ManyToMany

create table x4_employees(
    emp_id integer primary KEY
);


create table x4_projects(
    prj_id integer primary KEY
);

create table x4_emp_prj(
    emp_id integer REFERENCES x4_employees(emp_id) ON DELETE CASCADE,
    prj_id integer REFERENCES x4_projects(prj_id) ON DELETE CASCADE,
	
	primary key(emp_id, prj_id)
);

--
-- Unidirectional One-to-Many

create table x5_departments(
    dep_id integer primary key
);

create table x5_employees(
    emp_id integer primary key
);

create table x5_dep_emp(
    dep_id integer REFERENCES x4_projects(prj_id) ON DELETE CASCADE,
    emp_id integer REFERENCES x4_employees(emp_id) ON DELETE CASCADE unique,
	
	primary key(dep_id, emp_id)
);

DROP TABLE x5_dep_emp;


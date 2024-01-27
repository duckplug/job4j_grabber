CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO company (id, name) VALUES(1,'Company - 1');
INSERT INTO company (id, name) VALUES(2,'Company - 2');
INSERT INTO company (id, name) VALUES(3,'Company - 3');
INSERT INTO company (id, name) VALUES(4,'Company - 4');
INSERT INTO company (id, name) VALUES(5,'Company - 5');
INSERT INTO company (id, name) VALUES(6,'Company - 6');

INSERT INTO person (id, name, company_id) VALUES(1, 'Person A', 1);
INSERT INTO person (id, name, company_id) VALUES(2, 'Person B', 1);
INSERT INTO person (id, name, company_id) VALUES(3, 'Person C', 2);
INSERT INTO person (id, name, company_id) VALUES(4, 'Person D', 2);
INSERT INTO person (id, name, company_id) VALUES(5, 'Person E', 3);
INSERT INTO person (id, name, company_id) VALUES(6, 'Person F', 3);
INSERT INTO person (id, name, company_id) VALUES(7, 'Person G', 4);
INSERT INTO person (id, name, company_id) VALUES(8, 'Person H', 4);
INSERT INTO person (id, name, company_id) VALUES(9, 'Person I', 5);
INSERT INTO person (id, name, company_id) VALUES(10, 'Person J', 5);
INSERT INTO person (id, name, company_id) VALUES(11, 'Person K', 6);
INSERT INTO person (id, name, company_id) VALUES(12, 'Person L', 6);
INSERT INTO person (id, name, company_id) VALUES(13, 'Person M', 6);
INSERT INTO person (id, name, company_id) VALUES(14, 'Person N', 5)

select  p.name, c.name from person p join company c
on p.company_id = c.id where p.company_id <> 5;


select c.name, count(p.company_id) from company c
join person p on c.id = p.company_id group by c.name;
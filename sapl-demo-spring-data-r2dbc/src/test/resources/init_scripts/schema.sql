DROP TABLE IF EXISTS book;

CREATE TABLE book (id BIGINT PRIMARY KEY, name VARCHAR(255), category INT);

INSERT INTO book (id, name, category) VALUES (1, 'book1', 1);
INSERT INTO book (id, name, category) VALUES (2, 'book2', 1);
INSERT INTO book (id, name, category) VALUES (3, 'book3', 2);
INSERT INTO book (id, name, category) VALUES (4, 'book4', 3);
INSERT INTO book (id, name, category) VALUES (5, 'book5', 4);
INSERT INTO book (id, name, category) VALUES (6, 'book6', 5);

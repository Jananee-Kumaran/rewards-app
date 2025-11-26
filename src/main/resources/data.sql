INSERT INTO customers (id, name) VALUES (1, 'Alice Brown');
INSERT INTO customers (id, name) VALUES (2, 'John Smith');
INSERT INTO customers (id, name) VALUES (3, 'Bob Johnson');

INSERT INTO purchases (id, customer_id, amount, date) VALUES (1, 1, 120.0, '2025-11-02');
INSERT INTO purchases (id, customer_id, amount, date) VALUES (2, 1, 70.0, '2025-10-11');

INSERT INTO purchases (id, customer_id, amount, date) VALUES (3, 2, 200.0, '2025-11-12');
INSERT INTO purchases (id, customer_id, amount, date) VALUES (4, 2, 130.0, '2025-10-20');

INSERT INTO purchases (id, customer_id, amount, date) VALUES (5, 3, 80.0,  '2025-10-01');
INSERT INTO purchases (id, customer_id, amount, date) VALUES (6, 3, 120.0, '2025-11-21');

INSERT INTO customers (id, name) VALUES
  (1, 'John Doe'),
  (2, 'Alice Smith'),
  (3, 'Bob Johnson');

INSERT INTO transactions (id, customer_id, amount, date) VALUES
  (101, 1, 120.00, '2025-09-10'),
  (102, 1,  70.00, '2025-10-05'),
  (103, 1, 200.00, '2025-11-15'),
  (104, 1,  60.00, '2025-12-01');

INSERT INTO transactions (id, customer_id, amount, date) VALUES
  (201, 2,  95.00, '2025-09-20'),
  (202, 2, 130.00, '2025-10-28'),
  (203, 2, 220.00, '2025-11-18'),
  (204, 2,  75.00, '2025-12-02');

INSERT INTO transactions (id, customer_id, amount, date) VALUES
  (301, 3,  55.00, '2025-09-05'),
  (302, 3, 150.00, '2025-10-12'),
  (303, 3,  95.00, '2025-11-22'),
  (304, 3, 180.00, '2025-12-02');

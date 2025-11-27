INSERT INTO customers (id, name) VALUES
  (1, 'John Doe'),
  (2, 'Alice Smith'),
  (3, 'Bob Johnson');

INSERT INTO transactions (id, customer_id, amount, date) VALUES
  (101, 1, 120.00, '2024-01-10'),
  (102, 1, 80.00,  '2024-01-15'),
  (103, 1, 40.00,  '2024-02-02'),

  (201, 2, 200.00, '2024-02-05'),
  (202, 2, 130.00, '2024-03-10'),

  (301, 3, 55.00,  '2024-01-20'),
  (302, 3, 90.00,  '2024-02-25');

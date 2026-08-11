INSERT INTO customer (id, user_name, password, name, balance) OVERRIDING SYSTEM VALUE VALUES
    (1, 'Smith', '$2a$10$gRUMtCrgS8GNGjkfhPRAs.VALIDDj0IUJP4Zsh6JqYAOcZdU8nRUu', 'Josh Smith', 100.00),
    (2, 'Jane', '$2a$10$LnbvN9VIlWDtZXL6okG1qOkTW7GiGusDmwGThYVzvFDJvojBBhMfe', 'Jane Doe', 100.00),
    (3, 'John', '$2a$10$e.7LuA9c6EIBtgxVS8SbfOLMyQnUyop2IFSWC2Ot.ID6xO2tSvoqG', 'John Doe 1', 10.00);

INSERT INTO food (id, name, description, calorie, price) OVERRIDING SYSTEM VALUE VALUES
    (1, 'Fideua', 'Noodles gone wild in a seafood fiesta', 558.00, 15.00),
    (2, 'Paella', 'Rice party with a saffron twist', 379.00, 13.00),
    (3, 'Tortilla', 'A scrumptious flat floury flavor', 278.00, 10.00),
    (4, 'Gazpacho', 'Soup''s cold revenge for scorching summers', 162.00, 8.00),
    (5, 'Quesadilla', 'Cheesy tortilla hug with flavorful fillings', 470.00, 13.00);

INSERT INTO _order (id, price, timestamp_created, customer_id) OVERRIDING SYSTEM VALUE VALUES
    (1, 20.00, TIMESTAMP '2023-10-23 23:09:00', 1),
    (2, 39.00, TIMESTAMP '2023-10-24 12:00:00', 1);

INSERT INTO order_item (id, pieces, price, food_id, order_id) OVERRIDING SYSTEM VALUE VALUES
    (1, 2, 10.00, 3, 1),
    (2, 3, 13.00, 2, 2);

SELECT setval(pg_get_serial_sequence('customer', 'id'), (SELECT MAX(id) FROM customer));
SELECT setval(pg_get_serial_sequence('food', 'id'), (SELECT MAX(id) FROM food));
SELECT setval(pg_get_serial_sequence('_order', 'id'), (SELECT MAX(id) FROM _order));
SELECT setval(pg_get_serial_sequence('order_item', 'id'), (SELECT MAX(id) FROM order_item));

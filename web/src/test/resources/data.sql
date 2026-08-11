DELETE FROM order_item;
DELETE FROM _order;
DELETE FROM food;
DELETE FROM customer;

INSERT INTO customer(user_name, password, id, name, balance, version) VALUES
    ('Smith', '$2a$10$gRUMtCrgS8GNGjkfhPRAs.VALIDDj0IUJP4Zsh6JqYAOcZdU8nRUu', 1, 'Josh Smith', 100, 0),
    ('Jane', '$2a$10$LnbvN9VIlWDtZXL6okG1qOkTW7GiGusDmwGThYVzvFDJvojBBhMfe', 2, 'Jane Doe', 100, 0),
    ('John', '$2a$10$e.7LuA9c6EIBtgxVS8SbfOLMyQnUyop2IFSWC2Ot.ID6xO2tSvoqG', 3, 'John Doe 1', 10, 0);

INSERT INTO food(id, name, description, calorie, price) VALUES
    (1, 'Fideua', 'Noodles gone wild in a seafood fiesta', 558, 15),
    (2, 'Paella', 'Rice party with a saffron twist', 379, 13),
    (3, 'Tortilla', 'A scrumptious flat floury flavor', 278, 10),
    (4, 'Gazpacho', 'Soup''s cold revenge for scorching summers', 162, 8),
    (5, 'Quesadilla', 'Cheesy tortilla hug with flavorful fillings', 470, 13);

INSERT INTO _order(id, price, timestamp_created, customer_id) VALUES
    (1, 20, TIMESTAMP '2023-10-23 23:09:00', 1),
    (2, 39, TIMESTAMP '2023-10-24 12:00:00', 1);

INSERT INTO order_item(id, pieces, price, food_id, order_id) VALUES
    (1, 2, 10, 3, 1),
    (2, 3, 13, 2, 2);

ALTER TABLE _order ALTER COLUMN id RESTART WITH (SELECT MAX(id) FROM _order) + 1;
ALTER TABLE order_item ALTER COLUMN id RESTART WITH (SELECT MAX(id) FROM order_item) + 1;

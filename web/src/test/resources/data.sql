DELETE FROM order_item;
DELETE FROM _order;
DELETE FROM food;
DELETE FROM customer;

INSERT INTO customer(user_name, password, id, name, balance) VALUES
    ('Smith', 'SmithSecret', 1, 'Josh Smith', 100),
    ('Jane', 'JaneSecret', 2, 'Jane Doe', 100),
    ('John', 'JohnSecret', 3, 'John Doe 1', 10);

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

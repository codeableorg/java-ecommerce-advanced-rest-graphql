-- V2__Insert_sample_data.sql
-- Sample data for workshop demonstrations

-- Sample products
INSERT INTO products (name, description, price, category, image_url) VALUES
('Laptop Pro 15"', 'High-performance laptop for professionals', 1299.99, 'Electronics', 'https://example.com/laptop.jpg'),
('Wireless Headphones', 'Premium noise-canceling headphones', 199.99, 'Electronics', 'https://example.com/headphones.jpg'),
('Coffee Maker', 'Automatic drip coffee maker', 89.99, 'Appliances', 'https://example.com/coffee.jpg'),
('Running Shoes', 'Comfortable running shoes for daily training', 129.99, 'Sports', 'https://example.com/shoes.jpg'),
('Smartphone 128GB', 'Latest smartphone with advanced camera', 799.99, 'Electronics', 'https://example.com/phone.jpg'),
('Office Chair', 'Ergonomic office chair with lumbar support', 249.99, 'Furniture', 'https://example.com/chair.jpg'),
('Backpack 25L', 'Durable hiking backpack', 79.99, 'Sports', 'https://example.com/backpack.jpg'),
('Bluetooth Speaker', 'Portable waterproof speaker', 59.99, 'Electronics', 'https://example.com/speaker.jpg');

-- Sample users
INSERT INTO users (username, email, full_name) VALUES
('john_doe', 'john.doe@example.com', 'John Doe'),
('jane_smith', 'jane.smith@example.com', 'Jane Smith'),
('mike_wilson', 'mike.wilson@example.com', 'Mike Wilson'),
('sarah_brown', 'sarah.brown@example.com', 'Sarah Brown');

-- Sample inventory (stock for all products)
INSERT INTO inventory (product_id, stock_quantity) VALUES
(1, 25),  -- Laptop Pro 15"
(2, 50),  -- Wireless Headphones  
(3, 30),  -- Coffee Maker
(4, 75),  -- Running Shoes
(5, 40),  -- Smartphone 128GB
(6, 20),  -- Office Chair
(7, 35),  -- Backpack 25L
(8, 60);  -- Bluetooth Speaker

-- Sample orders
INSERT INTO orders (user_id, status, total_amount, order_date) VALUES
(1, 'DELIVERED', 1499.98, '2024-01-15 10:30:00'),
(2, 'SHIPPED', 329.98, '2024-01-20 14:45:00'),
(1, 'PENDING', 139.98, '2024-02-01 09:15:00'),
(3, 'CONFIRMED', 879.98, '2024-02-03 16:20:00'),
(4, 'DELIVERED', 59.99, '2024-02-05 11:00:00');

-- Sample order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
-- Order 1 (John): Laptop + Headphones
(1, 1, 1, 1299.99),
(1, 2, 1, 199.99),
-- Order 2 (Jane): Running Shoes + Backpack
(2, 4, 1, 129.99),
(2, 7, 1, 79.99),
-- Order 3 (John): Coffee Maker + Speaker  
(3, 3, 1, 89.99),
(3, 8, 1, 59.99),
-- Order 4 (Mike): Smartphone + Chair
(4, 5, 1, 799.99),
(4, 6, 1, 249.99),
-- Order 5 (Sarah): Bluetooth Speaker
(5, 8, 1, 59.99);

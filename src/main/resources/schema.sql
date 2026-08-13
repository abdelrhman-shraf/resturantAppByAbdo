CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(255)
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE menu_items (
    menu_item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    category_id INT
);
ALTER TABLE menu_items  ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES 
categories(category_id) ON DELETE SET NULL ;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_datetime DATETIME NOT NULL,
    status VARCHAR(30),
    payment_method VARCHAR(30),
    total_amount DECIMAL(10,2)
);
ALTER TABLE orders  ADD CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES 
customers(customer_id);

CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    menu_item_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2)
);
ALTER TABLE order_details  ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES 
orders(order_id);
ALTER TABLE order_details  ADD CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES 
menu_items(menu_item_id);
ALTER TABLE order_details ADD  subtotal DECIMAL(10,2)
 GENERATED ALWAYS AS (quantity * unit_price) STORED;
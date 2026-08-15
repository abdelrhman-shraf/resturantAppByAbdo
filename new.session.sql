USE resturant;
SELECT * FROM menu_items;
SELECT * FROM orders;
SELECT * FROM customers;
ALTER TABLE order_details ADD  subtotal DECIMAL(10,2)
 GENERATED ALWAYS AS (quantity * unit_price) STORED;
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(255)
);
UPDATE categories SET category_name="SFGD" WHERE category_id=1;
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);
SELECT * FROM categories;
CREATE TABLE menu_items (
    menu_item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    category_id INT
);
SELECT * FROM menu_items;
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
SELECT o.customer_id, COUNT(o.customer_id) AS num_of_orders,SUM(o.total_amount) AS total_money_spent
,c.first_name,c.last_name,c.phone,c.email FROM orders AS o
LEFT JOIN customers AS c ON c.customer_id=o.customer_id
 GROUP BY o.customer_id  LIMIT 1 ;

SELECT * FROM orders;
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
SELECT * FROM order_details;
SELECT o.order_id,o.order_datetime,o.status
,o.payment_method,o.total_amount,c.customer_id
,c.first_name,c.last_name,c.phone
,mi.menu_item_id,mi.item_name,od.quantity,od.unit_price,od.subtotal FROM orders AS o
LEFT JOIN customers AS c ON c.customer_id=o.customer_id
LEFT JOIN order_details AS od ON o.order_id=od.order_id
LEFT JOIN menu_items AS mi ON mi.menu_item_id=od.menu_item_id;
SELECT od.menu_item_id,mi.item_name,Sum(od.quantity) ,COUNT(od.menu_item_id),SUM(od.subtotal)  FROM order_details AS od
LEFT JOIN menu_items AS mi ON mi.menu_item_id=od.menu_item_id 
GROUP BY od.menu_item_id
ORDER BY sum_o DESC ;
SELECT COALESCE( SUM(o.total_amount),0) FROM orders AS o WHERE o.order_datetime<CURRENT_DATE ;
SELECT SUM(o.total_amount) FROM orders AS o WHERE WEEK(o.order_datetime) = WEEK(CURRENT_DATE) ;
SELECT SUM(o.total_amount) FROM orders AS o WHERE MONTH(o.order_datetime) = MONTH(CURRENT_DATE) ;


DELETE FROM orders WHERE order_id=1;

ALTER TABLE order_details  ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES 
orders(order_id);
ALTER TABLE order_details  ADD CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES 
menu_items(menu_item_id);

SHOW CREATE TABLE order_details;
DESCRIBE order_details;



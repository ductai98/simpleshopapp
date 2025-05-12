create database shopapp;
use shopapp;

create table users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(255) default '',
    phone_number varchar(10) not null,
    address varchar(255) default '',
    password VARCHAR(255) not null,
    created_at datetime,
    updated_at datetime,
    is_active tinyint(1) default 1,
    date_of_birth date,
    facebook_account_id int default 0,
    google_account_id int default 0
);

create table tokens(
    id int primary key AUTO_INCREMENT,
    token varchar(255) unique not null,
    token_type varchar(255) not null,
    revoked tinyint(1) not null,
    expired tinyint(1) not null,
    user_id int,
    created_at datetime,
    updated_at datetime,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

create table social_accounts(
    id int primary key AUTO_INCREMENT,
    user_id int,
    provider varchar(255) not null,
    provider_user_id varchar(255) not null,
    email varchar(255) not null,
    name varchar(255) not null,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

create table categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) not null default '',
    created_at datetime,
    updated_at datetime
);

create table products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) not null default '',
    description longtext default '',
    price DECIMAL(10, 2) not null check(price >= 0),
    quantity INT not null,
    image_url VARCHAR(255) default '',
    is_active tinyint(1) default 1,
    category_id INT,
    created_at datetime,
    updated_at datetime,
    foreign key (category_id) REFERENCES categories(id)
);

CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id),
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') ,
    total_money FLOAT CHECK(total_money >= 0),
    `shipping_method` VARCHAR(100),
    `shipping_address` VARCHAR(200),
    `shipping_date` DATE,
    `tracking_number` VARCHAR(100),
    `payment_method` VARCHAR(100),
    active TINYINT(1)
);

CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT ''
);
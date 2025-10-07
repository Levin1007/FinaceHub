-- financeHub.users definition

CREATE TABLE users (
id int(11) NOT NULL AUTO_INCREMENT,
name varchar(100) NOT NULL,
password varchar(100) NOT NULL,
balance varchar(100) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

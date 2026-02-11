CREATE DATABASE IF NOT EXISTS code_market;
USE code_market;

CREATE TABLE `member` (
                          `member_id` bigint NOT NULL AUTO_INCREMENT,
                          `email` varchar(255) NOT NULL,
                          `password` varchar(80) NOT NULL,
                          `username` varchar(50) NOT NULL,
                          PRIMARY KEY (`member_id`),
                          UNIQUE KEY `uk_member_email ` (`email`)
);

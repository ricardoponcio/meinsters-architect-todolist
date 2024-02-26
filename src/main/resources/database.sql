create database todolist;

use todolist;

create table todoitem (
    id integer not null AUTO_INCREMENT,
    title varchar(50) not null,
    description text null,
    status enum('PENDING', 'IN_PROGRESS', 'COMPLETED', 'DELETED') not null default 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);
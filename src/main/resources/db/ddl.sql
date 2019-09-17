drop table if exists resources;
drop table if exists roles;
drop table if exists users;
drop table if exists roles_resources;
drop table if exists users_roles;

create table resources(
descn varchar(100),
url varchar(120) not null unique,
parent_id bigint,
id bigint primary key );

create table roles(
name varchar(60) not null unique,
descn varchar(255),
is_sys tinyint default 0,
id bigint primary key );

create table users(
username varchar(30) not null unique,
password varchar(128),
host varchar(30),
regist_time datetime,
login_time datetime,
is_enabled tinyint default 1,
is_locked tinyint default 0,
lock_time datetime,
is_sys tinyint default 0,
mobile varchar(20),
openid varchar(48),
id bigint primary key );

create unique index idx_users_username on users(username);

create unique index idx_users_mobile on users(mobile);

create table roles_resources(
roles_id bigint,
resources_id bigint);

alter table roles_resources  add unique ( roles_id,resources_id);

create table users_roles(
users_id bigint,
roles_id bigint);

alter table users_roles  add unique ( users_id,roles_id);
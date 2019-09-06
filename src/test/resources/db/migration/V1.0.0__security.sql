create table users(
	username varchar(30) not null unique,
	password varchar(128),
	alias_ varchar(50) unique,
	host varchar(30),
	regist_time datetime,
	login_time datetime,
	is_enabled tinyint default 1,
	is_locked tinyint default 0,
	lock_time datetime,
	is_sys tinyint default 0,
	mobile varchar(20) unique,
	openid varchar(48),
	id bigint primary key );

create table roles(
	name varchar(60) not null unique,
	descn varchar(255),
	is_sys tinyint default 0,
	id bigint primary key );

create table resources(
    id bigint primary key,
    url varchar(120) not null unique,
    descn varchar(100),
    parent_id bigint );


create table users_roles(users_id bigint,roles_id bigint);

ALTER TABLE users_roles  ADD UNIQUE ( users_id,roles_id);

create table roles_resources(roles_id bigint,resources_id bigint);

ALTER TABLE roles_resources  ADD UNIQUE ( roles_id,resources_id);

insert into roles(id,name,descn,is_sys) values (0, 'administrator', '系统管理员角色', 1);
insert into users(id,username,password,regist_time,is_enabled,is_locked,is_sys) values (0, 'admin', 'f65921cf38f2e3eefde1a08d62a7656b',current_date(), 1, 0, 1);
insert into users_roles(users_id,roles_id) values (0,0);
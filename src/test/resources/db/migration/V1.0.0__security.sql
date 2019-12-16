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
	id bigint primary key, corp_id bigint );

create table roles(
	name varchar(60) not null unique,
	descn varchar(255),
	is_sys tinyint default 0,
	id bigint primary key,
	corp_id bigint);

create table resources(
    id bigint primary key,
    url varchar(120) not null unique,
    descn varchar(100),
    parent_id bigint );


create table users_roles(users_id bigint,roles_id bigint);

ALTER TABLE users_roles  ADD UNIQUE ( users_id,roles_id);

create table roles_resources(roles_id bigint,resources_id bigint);

ALTER TABLE roles_resources  ADD UNIQUE ( roles_id,resources_id);

insert into roles(id,name,descn,is_sys,corp_id) values (0, 'administrator', '系统管理员角色', 1, 0);
insert into users(id,username,password,regist_time,is_enabled,is_locked,is_sys, corp_id) values (0, 'admin', 'f65921cf38f2e3eefde1a08d62a7656b',current_date(), 1, 0, 1, 0);
insert into users_roles(users_id,roles_id) values (0,0);

alter table roles add column operator_id bigint;
alter table roles add column operator varchar(30);
alter table roles add column last_modified datetime default now();

alter table users add column operator_id bigint;
alter table users add column operator varchar(30);
alter table users add column last_modified datetime default now();
alter table users add column real_name varchar(30);
alter table users add column dept_id bigint;
alter table users add column dept_name varchar(30);
alter table users add column use_init_pass tinyint default 0;

create index idx_users_dept_id on users(dept_id);

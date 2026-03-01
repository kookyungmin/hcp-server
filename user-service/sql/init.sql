create database hcp_user;
# 사용자 마스터 테이블
create table hcp_user.h_user(
    user_id binary(16) primary key,
    display_name varchar(255) not null,
    status varchar(10) not null,
    is_master boolean default false, # true: master, false: sub
    master_user_id binary(16),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp
);

# 사용자 계정 테이블
create table hcp_user.h_user_account(
    user_id binary(16) not null primary key,
    email varchar(255) not null unique,
    password varchar(512) not null,
    last_changed_password_at timestamp default now(),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

# [서비스]:[권한(실행, 수정, 권한부여)]
create table hcp_user.h_permission(
    permission_code varchar(31) primary key,
    description varchar(31),
    is_active boolean default true,
    is_default boolean default false
);

insert into hcp_user.h_permission(permission_code, description, is_default)
values('user:read', '계정 조회 권한', true);

insert into hcp_user.h_permission(permission_code, description, is_default)
values('user:write', '계정 수정 권한', true);

insert into hcp_user.h_permission(permission_code, description, is_default)
values('user:grant', 'IAM 관리 권한', false);

insert into hcp_user.h_permission(permission_code, description, is_default)
values('compute:instance:execute', '인스턴스 실행 권한', false);

insert into hcp_user.h_permission(permission_code, description, is_default)
values('compute:instance:write', '인스턴스 생성/수정 권한', false);


select * from hcp_user.h_permission;

# 사용자 permission 테이블
create table hcp_user.h_user_permission(
    user_id binary(16) not null,
    permission_code varchar(31) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    primary key (user_id, permission_code)
);



select * from hcp_user.h_user;
select * from hcp_user.h_user_account;
select * from hcp_user.h_user_permission;
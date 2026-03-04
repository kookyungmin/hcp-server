create database hcp_compute;

create table hcp_compute.h_instance(
    instance_id binary(16) primary key,
    owner_id binary(16) not null,
    name varchar(32) not null,
    image varchar(16) not null,
    vpc varchar(16) not null,
    status varchar(16) not null,
    spec varchar(16) not null,
    storage_type varchar(5) not null,
    storage_size integer not null,
    failure_reason varchar(512),
    public_ip varchar(32),
    private_ip varchar(32),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table hcp_compute.h_instance_tag(
    instance_id binary(16) not null,
    tag varchar(256),
    primary key(instance_id, tag)
);


create table hcp_compute.h_instance_image(
    image_code varchar(16) primary key,
    image_name varchar(128) not null,
    image_description varchar(256),
    os_name varchar(16) not null,
    os_version varchar(16) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

insert into hcp_compute.h_instance_image(image_code, image_name, os_name, os_version, image_description)
values ('ubuntu:22.04', 'hcp_ubuntu:v1.0.0', 'Ubuntu', '22.04', 'Ubuntu 22.04 LTS');

insert into hcp_compute.h_instance_image(image_code, image_name, os_name, os_version, image_description)
values ('rocky:8.10', 'hcp_rocky:v1.0.0', 'Rocky Linux', '8.10', 'Rocky Linux 8.10 LTS');

create table hcp_compute.h_instance_spec(
    spec_code varchar(16) primary key,
    spec_name varchar(128) not null,
    spec_description varchar(256),
    cpu varchar(10) not null,
    memory varchar(10) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

insert into hcp_compute.h_instance_spec(spec_code, spec_name, spec_description, cpu, memory)
values ('h1.micro', 'h1.micro', 'CPU: 1cores, Memory: 256MB', '1', '256Mi');

insert into hcp_compute.h_instance_spec(spec_code, spec_name, spec_description, cpu, memory)
values ('h1.small', 'h1.small', 'CPU: 2cores, Memory: 512MB', '2', '512Mi');

insert into hcp_compute.h_instance_spec(spec_code, spec_name, spec_description, cpu, memory)
values ('h1.large', 'h1.large', 'CPU: 4cores, Memory: 2GB', '4', '2Gi');


create table hcp_compute.h_network_vpc(
    vpc_code varchar(16) primary key,
    vpc_name varchar(128) not null,
    vpc_description varchar(256),
    cidr_block varchar(18) not null,
    default_egress_policy varchar(16) not null,
    default_ingress_policy varchar(16) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

insert into hcp_compute.h_network_vpc(vpc_code, vpc_name, vpc_description, cidr_block, default_egress_policy, default_ingress_policy)
values('vpc-default', 'default VPC', '개발용 디폴트 VPC', '10.244.0.0/16', 'ALLOW_ALL', 'DENY_ALL');

select * from hcp_compute.h_network_vpc;

create table hcp_compute.h_idempotency_request(
    owner_id binary(16) not null,
    idempotency_key varchar(128) not null,
    command_type varchar(32) not null,
    request_hash varchar(512) not null,
    response varchar(1024),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    primary key(owner_id, idempotency_key)
);

create table hcp_compute.h_outbox_event(
    event_id binary(16) not null,
    event_type varchar(32) not null,
    payload varchar(1024) not null,
    status varchar(16) not null,
    retry_count smallint not null default 0,
    claim_token binary(16),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    primary key(event_id)
);





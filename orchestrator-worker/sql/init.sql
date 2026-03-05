create table hcp_worker.h_idempotency(
    idempotency_key binary(16) primary key,
    status varchar(32),
    expired_at timestamp,
    created_at timestamp default now(),
    updated_at timestamp default now()
);


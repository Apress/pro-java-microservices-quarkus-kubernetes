create sequence hibernate_sequence START WITH 100 INCREMENT BY 1;

create table "customers"
(
    "id"                 bigint    not null,
    "created_date"       timestamp not null,
    "last_modified_date" timestamp,
    "email"              varchar(255),
    "enabled"            boolean   not null,
    "first_name"         varchar(255),
    "last_name"          varchar(255),
    "telephone"          varchar(255)
);
alter table "customers"
    add constraint "customer_pk" primary key ("id");

create table "payments"
(
    "id"                 bigint         not null,
    "created_date"       timestamp      not null,
    "last_modified_date" timestamp,
    "transaction"        varchar(255),
    "status"             varchar(255)   not null,
    "amount"             decimal(10, 2) not null
);
alter table "payments"
    add constraint "payment_pk" primary key ("id");

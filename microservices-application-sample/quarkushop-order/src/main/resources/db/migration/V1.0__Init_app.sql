create sequence hibernate_sequence START WITH 100 INCREMENT BY 1;

create table "carts"
(
    "id"                 bigint       not null,
    "created_date"       timestamp    not null,
    "last_modified_date" timestamp,
    "status"             varchar(255) not null,
    "customer"           bigint
);
alter table "carts"
    add constraint "cart_pk" primary key ("id");

create table "order_items"
(
    "id"                 bigint    not null,
    "created_date"       timestamp not null,
    "last_modified_date" timestamp,
    "quantity"           bigint    not null,
    "order_id"           bigint,
    "product_id"         bigint
);
alter table "order_items"
    add constraint "order_item_pk" primary key ("id");

create table "orders"
(
    "id"                 bigint         not null,
    "created_date"       timestamp      not null,
    "last_modified_date" timestamp,
    "address_1"          varchar(255),
    "address_2"          varchar(255),
    "city"               varchar(255),
    "country"            varchar(2),
    "postcode"           varchar(10),
    "shipped"            timestamp,
    "status"             varchar(255)   not null,
    "total_price"        decimal(10, 2) not null,
    "cart_id"            bigint,
    "payment_id"         bigint
);
alter table "orders"
    add constraint "orders_pk" primary key ("id");
alter table "orders"
    add constraint "orders_uk" unique ("payment_id");
alter table "order_items"
    add constraint "order_item_fk2" foreign key ("order_id") references "orders" ("id");
alter table "orders"
    add constraint "orders_fk2" foreign key ("cart_id") references "carts" ("id");

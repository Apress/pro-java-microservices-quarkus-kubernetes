create sequence hibernate_sequence START WITH 100 INCREMENT BY 1;

create table "carts"
(
    "id"                 bigint       not null,
    "created_date"       timestamp    not null,
    "last_modified_date" timestamp,
    "status"             varchar(255) not null,
    "customer_id"        bigint
);
alter table "carts"
    add constraint "cart_pk" primary key ("id");

create table "categories"
(
    "id"                 bigint       not null,
    "created_date"       timestamp    not null,
    "last_modified_date" timestamp,
    "description"        varchar(255) not null,
    "name"               varchar(255) not null
);
alter table "categories"
    add constraint "categories_pk" primary key ("id");

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

create table "payments"
(
    "id"                 bigint         not null,
    "created_date"       timestamp      not null,
    "last_modified_date" timestamp,
    "paypal_payment_id"  varchar(255),
    "status"             varchar(255)   not null,
    "amount"             decimal(10, 2) not null
);
alter table "payments"
    add constraint "payment_pk" primary key ("id");

create table "products"
(
    "id"                 bigint         not null,
    "created_date"       timestamp      not null,
    "last_modified_date" timestamp,
    "description"        varchar(255)   not null,
    "name"               varchar(255)   not null,
    "price"              decimal(10, 2) not null,
    "sales_counter"      integer,
    "status"             varchar(255)   not null,
    "category_id"        bigint
);
alter table "products"
    add constraint "product_pk" primary key ("id");

create table "products_reviews"
(
    "product_id" bigint not null,
    "reviews_id" bigint not null
);
alter table "products_reviews"
    add constraint "products_reviews_pk" primary key ("product_id", "reviews_id");

create table "reviews"
(
    "id"                 bigint       not null,
    "created_date"       timestamp    not null,
    "last_modified_date" timestamp,
    "description"        varchar(255) not null,
    "rating"             bigint       not null,
    "title"              varchar(255) not null
);
alter table "reviews"
    add constraint "review_pk" primary key ("id");

alter table "products_reviews"
    add constraint "products_reviews_uk" unique ("reviews_id");
alter table "orders"
    add constraint "orders_uk" unique ("payment_id");
alter table "products_reviews"
    add constraint "products_reviews_fk1" foreign key ("reviews_id") references "reviews" ("id");
alter table "carts"
    add constraint "cart_fk" foreign key ("customer_id") references "customers" ("id");
alter table "order_items"
    add constraint "order_item_fk1" foreign key ("product_id") references "products" ("id");
alter table "orders"
    add constraint "orders_fk1" foreign key ("payment_id") references "payments" ("id");
alter table "order_items"
    add constraint "order_item_fk2" foreign key ("order_id") references "orders" ("id");
alter table "products"
    add constraint "product_fk" foreign key ("category_id") references "categories" ("id");
alter table "orders"
    add constraint "orders_fk2" foreign key ("cart_id") references "carts" ("id");
alter table "products_reviews"
    add constraint "products_reviews_fk2" foreign key ("product_id") references "products" ("id");

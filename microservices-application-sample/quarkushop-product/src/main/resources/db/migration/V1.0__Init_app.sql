create sequence hibernate_sequence START WITH 100 INCREMENT BY 1;

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
alter table "products_reviews"
    add constraint "products_reviews_fk1" foreign key ("reviews_id") references "reviews" ("id");
alter table "products"
    add constraint "product_fk" foreign key ("category_id") references "categories" ("id");
alter table "products_reviews"
    add constraint "products_reviews_fk2" foreign key ("product_id") references "products" ("id");

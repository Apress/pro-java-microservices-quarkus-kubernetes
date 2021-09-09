insert into "categories"
values (1, current_timestamp, current_timestamp, 'Phones & Smartphones', 'Mobile'),
       (2, current_timestamp, current_timestamp, 'Computers and Laptops', 'PC');

insert into "products"
values (1, current_timestamp, current_timestamp, 'The latest powerful iPhone from Apple', 'iPhone 11 Pro', 999.00, 0,
        'AVAILABLE', 1),
       (2, current_timestamp, current_timestamp, 'The most powerful iPhone from Apple', 'iPhone XS', 759.00, 0,
        'AVAILABLE', 1),
       (3, current_timestamp, current_timestamp, 'The most powerful MacBook from Apple', 'MacBook Pro 13', 1999.00, 0,
        'AVAILABLE', 2),
       (4, current_timestamp, current_timestamp, 'The phone that gets it done!', 'Google Pixel 4', 450.00, 0,
        'AVAILABLE', 1);

insert into "reviews"
values (1, current_timestamp, current_timestamp, 'I like the product but I found that it''s not perfect', 4,
        'Good but not perfect'),
       (2, current_timestamp, current_timestamp, 'Wonderful product', 5, 'Excellent'),
       (3, current_timestamp, current_timestamp, 'I like the product but not the price', 4, 'Good but very expensive');

insert into "products_reviews"
values (1, 1),
       (1, 2),
       (2, 3);

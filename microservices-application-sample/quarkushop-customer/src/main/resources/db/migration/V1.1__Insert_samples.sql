insert into "customers"
values (1, current_timestamp, current_timestamp, 'jason.bourne@mail.hello', TRUE, 'Jason', 'Bourne', '010203040506'),
       (2, current_timestamp, current_timestamp, 'homer.simpson@mail.hello', TRUE, 'Homer', 'Simpson', '060504030201'),
       (3, current_timestamp, current_timestamp, 'peter.quinn@mail.hello', FALSE, 'Peter', 'Quinn', '070605040302'),
       (4, current_timestamp, current_timestamp, 'saul.berenson@mail.hello', FALSE, 'Saul', 'Berenson', '070503010204'),
       (5, current_timestamp, current_timestamp, 'nebrass@quarkushop.store', TRUE, 'Nebrass', 'Lamouchi', '0606060606');

insert into payments
values (1, current_timestamp, current_timestamp, 'somePaymentId', 'ACCEPTED', 999.00),
       (2, current_timestamp, current_timestamp, 'paymentId', 'ACCEPTED', 759.00),
       (4, current_timestamp, current_timestamp, 'paymentId', 'ACCEPTED', 759.00);

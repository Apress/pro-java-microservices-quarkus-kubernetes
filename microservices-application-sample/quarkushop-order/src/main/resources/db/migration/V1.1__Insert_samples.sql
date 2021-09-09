insert into "carts"
values (1, current_timestamp, current_timestamp, 'NEW', 1),
       (2, current_timestamp, current_timestamp, 'NEW', 2),
       (3, current_timestamp, current_timestamp, 'NEW', 3),
       (4, current_timestamp, current_timestamp, 'NEW', 4),
       (5, current_timestamp, current_timestamp, 'NEW', 5);

insert into "orders"
values (1, current_timestamp, current_timestamp, '1 Rue Vaugirard', NULL, 'Paris', 'FR', '75015', NULL, 'PAID',
        999.00, 1, 2),
       (2, current_timestamp, current_timestamp, '2 Rue Maupertuis', NULL, 'Le Mans', 'FR', '72100', NULL, 'PAID',
        759.00,
        2, 1),
       (3, current_timestamp, current_timestamp, '39 Quai du Président Roosevelt', NULL, 'Issy-les-Moulineaux', 'FR',
        92130, NULL, 'CREATION', 0, 3, NULL),
       (4, current_timestamp, current_timestamp, 'Cité Safia 2', NULL, 'Ksour', 'TN',
        7160, NULL, 'CREATION', 0, 4, NULL),
       (5, current_timestamp, current_timestamp, 'Cité Safia 2', NULL, 'Ksour', 'TN',
        7160, NULL, 'CREATION', 0, 5, 4);

insert into "order_items"
values (1, current_timestamp, current_timestamp, 1, 1, 1),
       (2, current_timestamp, current_timestamp, 1, 2, 2);
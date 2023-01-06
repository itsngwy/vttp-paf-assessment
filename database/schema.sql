CREATE SCHEMA `eshop` ;

create table eshop.customers (
	name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,
    
    primary key(name)
);

create table eshop.order (

    order_id        char(8)           not null,
    name 			varchar(128) 	  not null,
    order_date 		date              not null,

    primary key(order_id)
);

create table eshop.lineitem (

	item_id int auto_increment not null,
	item text not null,
    quantity int default '1',
    
    order_id char(8) not null,
    
    primary key(item_id),
    
    constraint fk_order_id
        foreign key(order_id) references eshop.order(order_id)
);

create table eshop.order_status (

    order_id char(8) not null,

	delivery_id varchar(128),
	status enum('pending', 'dispatched')  default 'pending',
    status_update date not null,

    primary key(order_id)
);
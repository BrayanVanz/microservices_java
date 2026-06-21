create table tb_product (
    id serial not null,
    title varchar(255) not null,
    artist varchar(255) not null,
    release_date date,
    genre varchar(50) not null,
    is_active boolean not null default true,
    category varchar(20) not null,
    currency varchar(3) not null,
    price float(53) not null,
    stock integer not null,
    image_url varchar(255),
    primary key (id)
);

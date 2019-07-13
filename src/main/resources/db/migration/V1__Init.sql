create table message
(
    id        bigint not null,
    date      timestamp,
    removed   boolean,
    text      varchar(255),
    author_id bigint,
    primary key (id)
);

create table theme
(
    id        bigint not null,
    date      timestamp,
    removed   boolean,
    title     varchar(255),
    author_id bigint,
    primary key (id)
);

create table theme_messages
(
    theme_id    bigint not null,
    messages_id bigint not null
);

create table users
(
    id         bigint not null,
    first_name varchar(255),
    last_name  varchar(255),
    password   varchar(255),
    role       varchar(255),
    username   varchar(255),
    primary key (id)
);


CREATE SEQUENCE hibernate_sequence INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
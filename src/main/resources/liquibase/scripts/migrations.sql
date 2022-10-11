-- liquibase formatted sql

-- changeset oalekseenko:1
CREATE TABLE notification_task (
    id int generated by default as identity primary key,
    chat_id bigint not null,
    notification_message text not null,
    notification_date timestamp not null,
    is_done boolean not null default false
);
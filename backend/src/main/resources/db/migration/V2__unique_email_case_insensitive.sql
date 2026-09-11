update users set email = lower(email);
alter table users drop constraint uq_users_email;
create unique index uq_users_email_lower on users (lower(email));
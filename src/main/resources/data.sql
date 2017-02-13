INSERT INTO user (username, email, password) VALUES ('user', 'user@user', '$2a$08$abOsyGdgBFbuM4Z83IwzIuX0PwNpm07Sapqy/JZqXlP9FNDR98eSu')
INSERT INTO user (username, email, password) VALUES ('admin', 'admin@admin', '$2a$08$zAg6oF4ZjT3yuU5bFxr/qu0o6bIdYiH5BGnW/tKoUYXKxsUMe4YRy')
INSERT INTO role (name) VALUES ('ROLE_USER')
INSERT INTO role (name) VALUES ('ROLE_ADMIN')
INSERT INTO user_role (user_id, roles_id) VALUES ('user',1)
INSERT INTO user_role (user_id, roles_id) VALUES ('admin',1)
INSERT INTO user_role (user_id, roles_id) VALUES ('admin',2)
INSERT INTO message (body, read, recipient_name, sender_name, sent, sent_date, subject, user_username) VALUES ('example message body', true, 'admin', 'user', false, null, 'example subject', 'user')
INSERT INTO message (body, read, recipient_name, sender_name, sent, sent_date, subject, user_username) VALUES ('hello in app', false, 'user', 'admin', true, CURRENT_DATE, 'Hello', 'user' )

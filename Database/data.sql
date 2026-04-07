INSERT INTO operations (title) VALUES ('CREATE'), ('DELETE'), ('READ'), ('UPDATE');

INSERT INTO roles (title) VALUES ('ROLE_OWNER'), ('ROLE_COLLABORATOR'), ('ROLE_MEMBER'), ('ROLE_GUEST');

INSERT INTO resources (title) VALUES ('NOTEBOOK'), ('NOTE'), ('TAG'), ('COLLABORATOR'), ('INVITATION');

INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES
(1,1,1),(1,1,2),(1,1,3),(1,1,4),(1,2,1),(1,2,2),(1,2,3),(1,2,4),
(1,3,1),(1,3,2),(1,3,3),(1,3,4),(1,4,1),(1,4,2),(1,4,3),(1,4,4),
(1,5,1),(1,5,2),(1,5,3),(1,5,4),(2,1,1),(2,1,2),(2,1,3),(2,1,4),
(2,2,1),(2,2,2),(2,2,3),(2,2,4),(2,3,1),(2,3,2),(2,3,3),(2,3,4),
(2,4,1),(2,4,2),(2,4,3),(2,4,4),(2,5,1),(2,5,2),(2,5,3),(2,5,4),
(3,1,1),(3,1,2),(3,1,3),(3,1,4),(3,2,1),(3,2,2),(3,2,3),(3,2,4),
(3,3,1),(3,3,2),(3,3,3),(3,3,4),(3,4,1),(3,4,2),(3,4,3),(3,4,4),
(3,5,1),(3,5,2),(3,5,3),(3,5,4),(4,1,1),(4,1,2),(4,1,3),(4,1,4),
(4,2,1),(4,2,2),(4,2,3),(4,2,4),(4,3,1),(4,3,2),(4,3,3),(4,3,4),
(4,4,1),(4,4,2),(4,4,3),(4,4,4),(4,5,1),(4,5,2),(4,5,3),(4,5,4);

INSERT INTO resource_acl (user_id, role_id, resource_id, resource_instance_id) VALUES
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'd3c58262-9ac8-45bf-9427-68175eb3e3cf'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '6cfcab92-98a4-4bbc-b7f2-3bbd3b399f98'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '7d957871-1fd8-4f13-a108-56b20f34f9e3'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'f6c576ca-ad6c-429c-824b-292164bcf9a9'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '7f3dbf68-b66a-461a-b867-db34886984e8'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '6db34591-55c6-44d1-aff2-846023bb1128'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'cb5bffbd-0478-434a-b826-bc0feea0d843'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'e1988a35-5272-40bd-9964-f2d2ee631022'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '5dfa917e-4acf-44eb-a1c8-cbafd246b64d'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'e940a06d-8ee7-4a7e-ac9c-ce530e7f7969'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, '154521d1-dd08-4f9d-9c18-18da13e1fe9c'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'ac4f140e-e463-4256-bd95-413c0997490f'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'ffad2e19-df22-4765-b94a-a18ada502d95'),
('abf76d59-c7d5-42b4-ab9d-b542993f7496', 1, 1, 'a3c1172c-9063-4e27-ac1a-d2bb1722ddab');

INSERT INTO user_status_codes (title) VALUES ('active'), ('inactive');

INSERT INTO invitation_status_codes (title) VALUES ('pending'), ('accepted'), ('rejected'), ('resend');

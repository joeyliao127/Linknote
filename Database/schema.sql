-- Convention 說明
-- 1. Table name, column name 使用 snake 命名
-- 2. Table name 使用負數形式命名，避免與保留字衝突，如 order, group

-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS notebook_user_roles CASCADE;
DROP TABLE IF EXISTS note_tags CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS invitation_status_codes CASCADE;
DROP TABLE IF EXISTS user_status_codes CASCADE;
DROP TABLE IF EXISTS invitations CASCADE;
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS notebooks CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS access_policies CASCADE;
DROP TABLE IF EXISTS operations CASCADE;
DROP TABLE IF EXISTS resources CASCADE;

CREATE TABLE operations (
  id            INT          GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(50)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (title),
  PRIMARY KEY (id)
);

INSERT INTO operations (title) VALUES ('CREATE'), ('DELETE'), ('READ'), ('UPDATE');

CREATE TABLE roles (
  id            INT          GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(50)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (title),
  PRIMARY KEY (id)
);

INSERT INTO roles (title) VALUES ('ROLE_OWNER'), ('ROLE_COLLABORATOR'), ('ROLE_MEMBER'), ('ROLE_GUEST');

CREATE TABLE resources (
  id            INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(50)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (title),
  PRIMARY KEY (id)
);

INSERT INTO resources (title) VALUES ('NOTEBOOK'), ('NOTE'), ('TAG'), ('COLLABORATOR'), ('INVITATION');

CREATE TABLE role_permissions (
  id                INT       GENERATED ALWAYS AS IDENTITY,
  role_id           INT       NOT NULL,
  resource_id       INT       NOT NULL,
  operation_id      INT       NOT NULL,
  created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_role_permission_role_id           FOREIGN KEY (role_id)           REFERENCES roles (id),
  CONSTRAINT fk_role_permission_resource_id       FOREIGN KEY (resource_id)       REFERENCES resources (id),
  CONSTRAINT fk_role_permission_operation_id      FOREIGN KEY (operation_id)      REFERENCES operations (id),
  PRIMARY KEY (id)
);

CREATE INDEX idx_role_permission_resource      ON role_permissions (resource_id);
CREATE INDEX idx_role_permission_role          ON role_permissions (role_id);

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


CREATE TABLE resource_acl (
  id                           BIGINT       GENERATED ALWAYS AS IDENTITY,
  user_id                      UUID         NOT NULL,
  role_id                      INT          NOT NULL,
  resource_id                  INT          NOT NULL,
  resource_instance_id         UUID         NOT NULL,

  created_at     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (user_id, resource_id, resource_instance_id),

  CONSTRAINT fk_acl_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
  CONSTRAINT fk_acl_resource_id FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE
);

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


CREATE INDEX idx_acl_user_resource ON resource_acl (user_id, resource_id, resource_instance_id);


CREATE TABLE user_status_codes(
  id            INT           GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(255)  NOT NULL,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

INSERT INTO user_status_codes (title) VALUES ('active'), ('inactive');

CREATE TABLE users (
  id              UUID          NOT NULL,
  username        VARCHAR(50)   NOT NULL,
  email           VARCHAR(320)  NOT NULL,
  password        VARCHAR(255)  NOT NULL,
  user_status_id  INT           NOT NULL,
  created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_status_id FOREIGN KEY (user_status_id) REFERENCES user_status_codes (id),
  UNIQUE (email),
  PRIMARY KEY (id)
)

CREATE INDEX idx_users_email ON Users (email);

CREATE TABLE user_roles (
  role_id       INT           NOT NULL,
  user_id       UUID          NOT NULL,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_role_id  FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_role_user_id  FOREIGN KEY (role_id) REFERENCES roles (id),
  PRIMARY KEY (role_id, user_id)
);

CREATE TABLE notebooks (
  id            UUID          NOT NULL,
  title         VARCHAR(50)   NOT NULL,
  description   TEXT,
  is_active     BOOLEAN       DEFAULT TRUE,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notebooks_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
  PRIMARY KEY (id)
);

CREATE INDEX idx_notebooks_user_id ON notebooks (user_id);
CREATE INDEX idx_notebooks_name ON notebooks (title);

CREATE TABLE invitation_status_codes (
  id            INT           GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(255)  NOT NULL,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

INSERT INTO invitation_status_codes (title) VALUES ('pending'), ('accepted'), ('rejected'), ('resend');

CREATE TABLE invitations (
  id                    UUID  NOT NULL,
  inviter_id            UUID  NOT NULL,
  invitee_id            UUID  NOT NULL,
  message               TEXT,
  invitation_status_id  INT           NOT NULL,
  created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  notebook_id           UUID          NOT NULL,
  CONSTRAINT fk_invitations_notebook_id    FOREIGN KEY (notebook_id)            REFERENCES notebooks (id)               ON DELETE CASCADE,
  CONSTRAINT fk_invitations_inviter_id  FOREIGN KEY (inviter_id)          REFERENCES users (id)                ON DELETE CASCADE,
  CONSTRAINT fk_invitations_invitee_id  FOREIGN KEY (invitee_id)          REFERENCES users (id)                ON DELETE CASCADE,
  CONSTRAINT fk_invitations_status_code    FOREIGN KEY (invitation_status_id)   REFERENCES invitation_status_codes (id) ON DELETE RESTRICT,
  PRIMARY KEY (id)
);

CREATE INDEX idx_invitations_notebook_id   ON invitations (notebook_id);
CREATE INDEX idx_invitations_inviter_id ON invitations (inviter_id);
CREATE INDEX idx_invitations_invitee_id ON invitations (invitee_id);

CREATE TABLE notebook_user_roles (
  id            BIGINT      GENERATED ALWAYS AS IDENTITY,
  notebook_id   UUID        NOT NULL,
  user_id       UUID        NOT NULL,
  role_id       INT         NOT NULL,
  created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notebooks_users_role_notebook_id FOREIGN KEY (notebook_id)  REFERENCES notebooks (id) ON DELETE CASCADE,
  CONSTRAINT fk_notebooks_users_role_user_id     FOREIGN KEY (user_id)      REFERENCES users (id)     ON DELETE CASCADE,
  CONSTRAINT fk_notebooks_users_role_role_id     FOREIGN KEY (role_id)      REFERENCES roles (id)     ON DELETE CASCADE,
  PRIMARY KEY (user_id, notebook_id),
  UNIQUE (id)
);

CREATE INDEX idx_notebooks_users_role_notebook_id ON notebook_user_roles (notebook_id);
CREATE INDEX idx_notebooks_users_role_role_id     ON notebook_user_roles (role_id);
CREATE INDEX idx_notebooks_users_role_user_id     ON notebook_user_roles (user_id);
CREATE INDEX idx_notebook_user_roles_composite    ON notebook_user_roles (user_id, notebook_id, role_id);

CREATE TABLE notes (
  id            UUID         NOT NULL,
  title         VARCHAR(100) DEFAULT 'new Note',
  question      TEXT,
  content       TEXT,
  keypoint      TEXT,
  star          BOOLEAN     DEFAULT FALSE,
  created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  notebook_id   UUID        NOT NULL,
  CONSTRAINT fk_notes_notebook_id FOREIGN KEY (notebook_id) REFERENCES notebooks (id) ON DELETE CASCADE,
  PRIMARY KEY (id)
);

CREATE INDEX idx_notes_title ON notes (title);
CREATE INDEX idx_notes_notebook_id ON notes (notebook_id);

DROP TABLE IF EXISTS tags;
CREATE TABLE tags (
  id            UUID        NOT NULL,
  title         VARCHAR(50) NOT NULL,
  user_id       UUID        NOT NULL,
  created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (title, user_id),
  CONSTRAINT fk_tags_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_tags_title ON tags (title);
CREATE INDEX idx_tags_user_id ON tags (user_id);

DROP TABLE IF EXISTS note_tags;
CREATE TABLE note_tags (
  note_id       UUID        NOT NULL,
  tag_id        UUID        NOT NULL,       -- ✅ 參照 tags(id)
  created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (note_id, tag_id),           -- ✅ 複合主鍵
  CONSTRAINT fk_note_tags_note_id FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
  CONSTRAINT fk_note_tags_tag_id FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE INDEX idx_note_tags_note_id ON note_tags (note_id);
CREATE INDEX idx_note_tags_tag_id ON note_tags (tag_id);
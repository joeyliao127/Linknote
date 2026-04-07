-- Convention 說明
-- 1. Table name, column name 使用 snake 命名
-- 2. Table name 使用負數形式命名，避免與保留字衝突，如 order, group

-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS note_interactions CASCADE;
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

CREATE TABLE roles (
  id            INT          GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(50)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (title),
  PRIMARY KEY (id)
);

CREATE TABLE resources (
  id            INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(50)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (title),
  PRIMARY KEY (id)
);

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

CREATE INDEX idx_acl_user_resource ON resource_acl (user_id, resource_id, resource_instance_id);


CREATE TABLE user_status_codes(
  id            INT           GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(255)  NOT NULL,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

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
);

CREATE INDEX idx_users_email ON users (email);

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
  PRIMARY KEY (id)
);

CREATE INDEX idx_notebooks_name ON notebooks (title);

CREATE TABLE invitation_status_codes (
  id            INT           GENERATED ALWAYS AS IDENTITY,
  title         VARCHAR(255)  NOT NULL,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

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
  view_count    BIGINT      NOT NULL DEFAULT 0,
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

DROP TABLE IF EXISTS rag_notes;
CREATE TABLE rag_notes (
  note_id         UUID        NOT NULL,
  user_id         UUID        NOT NULL,
  notebook_id     UUID        NOT NULL,
  note_updated_at TIMESTAMP   NOT NULL,
  created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (note_id),
  CONSTRAINT fk_rag_notes_note_id     FOREIGN KEY (note_id)     REFERENCES notes (id)     ON DELETE CASCADE,
  CONSTRAINT fk_rag_notes_user_id     FOREIGN KEY (user_id)     REFERENCES users (id)     ON DELETE CASCADE,
  CONSTRAINT fk_rag_notes_notebook_id FOREIGN KEY (notebook_id) REFERENCES notebooks (id) ON DELETE CASCADE
);

CREATE INDEX idx_rag_notes_user_id     ON rag_notes (user_id);
CREATE INDEX idx_rag_notes_notebook_id ON rag_notes (notebook_id);


DROP TABLE IF EXISTS chat_session;
CREATE TABLE chat_session (
  note_id         UUID        NOT NULL,
  user_id         UUID        NOT NULL,
  notebook_id     UUID        NOT NULL,
  note_updated_at TIMESTAMP   NOT NULL,
  created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (note_id),
  CONSTRAINT fk_rag_notes_note_id     FOREIGN KEY (note_id)     REFERENCES notes (id)     ON DELETE CASCADE,
  CONSTRAINT fk_rag_notes_user_id     FOREIGN KEY (user_id)     REFERENCES users (id)     ON DELETE CASCADE,
  CONSTRAINT fk_rag_notes_notebook_id FOREIGN KEY (notebook_id) REFERENCES notebooks (id) ON DELETE CASCADE
);

CREATE TABLE note_interactions (
  id        BIGINT                   GENERATED ALWAYS AS IDENTITY,
  user_id   UUID                     NOT NULL,
  note_id   UUID                     NOT NULL,
  action    VARCHAR(20)              NOT NULL,
  acted_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_note_interactions_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_note_interactions_note_id FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
  PRIMARY KEY (id)
);

-- Per-minute deduplication: same user + note + action within the same minute = 1 record
CREATE UNIQUE INDEX uidx_note_interactions_dedup
    ON note_interactions (
        user_id, 
        note_id, 
        action, 
        date_trunc('minute', acted_at AT TIME ZONE 'UTC')
    );

CREATE INDEX idx_note_interactions_note_id  ON note_interactions (note_id);
CREATE INDEX idx_note_interactions_user_id  ON note_interactions (user_id);
CREATE INDEX idx_note_interactions_acted_at ON note_interactions (acted_at);
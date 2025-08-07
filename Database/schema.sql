-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS role_permission CASCADE;
DROP TABLE IF EXISTS notes_tags CASCADE;
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS notebooks_users_role CASCADE;
DROP TABLE IF EXISTS invitations CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS notebooks CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS target CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS behavior CASCADE;
DROP TABLE IF EXISTS action CASCADE;

-- Create action table
CREATE TABLE action (
  id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- Insert action data
INSERT INTO action (name) VALUES ('ALLOW'), ('DENY');

-- Create behavior table
CREATE TABLE behavior (
  id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- Insert behavior data
INSERT INTO behavior (name) VALUES ('CREATE'), ('DELETE'), ('read'), ('UPDATE');

-- Create users table
CREATE TABLE users (
  id VARCHAR(36) NOT NULL,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(32) NOT NULL,
  status BOOLEAN NOT NULL DEFAULT TRUE,
  create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (email)
);

-- Create index on users
CREATE INDEX idx_users_email ON users (email);

-- Create notebooks table
CREATE TABLE notebooks (
  id VARCHAR(34) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description TEXT,
  status BOOLEAN DEFAULT TRUE,
  userId VARCHAR(36) NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_notebooks_userId FOREIGN KEY (userId) REFERENCES users (id) ON DELETE RESTRICT
);

-- Create indexes on notebooks
CREATE INDEX idx_notebooks_userId ON notebooks (userId);
CREATE INDEX idx_notebooks_name ON notebooks (name);

-- Create role table
CREATE TABLE role (
  id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- Insert role data
INSERT INTO role (name) VALUES ('ROLE_OWNER'), ('ROLE_COLLABORATOR'), ('ROLE_MEMBER'), ('ROLE_GUEST');

-- Create target table
CREATE TABLE target (
  id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- Insert target data
INSERT INTO target (name) VALUES ('NOTEBOOK'), ('NOTE'), ('TAG'), ('COLLABORATOR'), ('INVITATION');

-- Create invitations table
CREATE TABLE invitations (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  inviterEmail VARCHAR(50) NOT NULL,
  inviteeEmail VARCHAR(50) NOT NULL,
  notebookId VARCHAR(34) NOT NULL,
  message TEXT,
  isAccept BOOLEAN NOT NULL DEFAULT FALSE,
  isPending BOOLEAN NOT NULL DEFAULT TRUE,
  date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_invitations_notebookId FOREIGN KEY (notebookId) REFERENCES notebooks (id) ON DELETE CASCADE,
  CONSTRAINT fk_invitations_inviterEmail FOREIGN KEY (inviterEmail) REFERENCES users (email) ON DELETE CASCADE,
  CONSTRAINT fk_invitations_inviteeEmail FOREIGN KEY (inviteeEmail) REFERENCES users (email) ON DELETE CASCADE
);

-- Create indexes on invitations
CREATE INDEX idx_invitations_notebookId ON invitations (notebookId);
CREATE INDEX idx_invitations_inviterEmail ON invitations (inviterEmail);
CREATE INDEX idx_invitations_inviteeEmail ON invitations (inviteeEmail);

-- Create notebooks_users_role table
CREATE TABLE notebooks_users_role (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  notebookId VARCHAR(34) NOT NULL,
  userId VARCHAR(36) NOT NULL,
  roleId SMALLINT NOT NULL,
  PRIMARY KEY (userId, notebookId),
  UNIQUE (id),
  CONSTRAINT fk_notebooks_users_role_notebookId FOREIGN KEY (notebookId) REFERENCES notebooks (id) ON DELETE CASCADE,
  CONSTRAINT fk_notebooks_users_role_userId FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_notebooks_users_role_roleId FOREIGN KEY (roleId) REFERENCES role (id) ON DELETE CASCADE
);

-- Create indexes on notebooks_users_role
CREATE INDEX idx_notebooks_users_role_notebookId ON notebooks_users_role (notebookId);
CREATE INDEX idx_notebooks_users_role_roleId ON notebooks_users_role (roleId);
CREATE INDEX idx_notebooks_users_role_userId ON notebooks_users_role (userId);

-- Create notes table
CREATE TABLE notes (
  id VARCHAR(33) NOT NULL,
  name VARCHAR(50) DEFAULT 'new Note',
  question TEXT,
  content TEXT,
  keypoint TEXT,
  createDate TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  star BOOLEAN DEFAULT FALSE,
  notebookId VARCHAR(34) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_notes_notebookId FOREIGN KEY (notebookId) REFERENCES notebooks (id) ON DELETE CASCADE
);

-- Create indexes on notes
CREATE INDEX idx_notes_name ON notes (name);
CREATE INDEX idx_notes_notebookId ON notes (notebookId);

-- Create tags table
CREATE TABLE tags (
  id VARCHAR(33) NOT NULL,
  name VARCHAR(50) NOT NULL,
  notebookId VARCHAR(34) NOT NULL,
  PRIMARY KEY (name, notebookId),
  UNIQUE (id),
  CONSTRAINT fk_tags_notebookId FOREIGN KEY (notebookId) REFERENCES notebooks (id) ON DELETE CASCADE
);

-- Create indexes on tags
CREATE INDEX idx_tags_name ON tags (name);
CREATE INDEX idx_tags_notebookId ON tags (notebookId);

-- Create notes_tags table
CREATE TABLE notes_tags (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  noteId VARCHAR(33) NOT NULL,
  tagId VARCHAR(33) NOT NULL,
  PRIMARY KEY (tagId, noteId),
  UNIQUE (id),
  CONSTRAINT fk_notes_tags_noteId FOREIGN KEY (noteId) REFERENCES notes (id) ON DELETE CASCADE,
  CONSTRAINT fk_notes_tags_tagId FOREIGN KEY (tagId) REFERENCES tags (id) ON DELETE CASCADE
);

-- Create index on notes_tags
CREATE INDEX idx_notes_tags_noteId ON notes_tags (noteId);

-- Create role_permission table
CREATE TABLE role_permission (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  roleId SMALLINT NOT NULL,
  target SMALLINT NOT NULL,
  behaviorId SMALLINT NOT NULL,
  actionId SMALLINT NOT NULL,
  UNIQUE (id),
  CONSTRAINT fk_role_permission_roleId FOREIGN KEY (roleId) REFERENCES role (id),
  CONSTRAINT fk_role_permission_actionId FOREIGN KEY (actionId) REFERENCES action (id),
  CONSTRAINT fk_role_permission_target FOREIGN KEY (target) REFERENCES target (id)
);

-- Create indexes on role_permission
CREATE INDEX idx_role_permission_actionId ON role_permission (actionId);
CREATE INDEX idx_role_permission_target ON role_permission (target);
CREATE INDEX idx_role_permission_roleId ON role_permission (roleId);

-- Insert role_permission data
INSERT INTO role_permission (roleId, target, behaviorId, actionId) VALUES
(1,1,1,1),(1,1,2,1),(1,1,3,1),(1,1,4,1),(1,2,1,1),(1,2,2,1),(1,2,3,1),(1,2,4,1),
(1,3,1,1),(1,3,2,1),(1,3,3,1),(1,3,4,1),(1,4,1,1),(1,4,2,1),(1,4,3,1),(1,4,4,1),
(1,5,1,1),(1,5,2,1),(1,5,3,1),(1,5,4,1),(2,1,1,2),(2,1,2,1),(2,1,3,2),(2,1,4,2),
(2,2,1,1),(2,2,2,1),(2,2,3,1),(2,2,4,1),(2,3,1,1),(2,3,2,1),(2,3,3,1),(2,3,4,1),
(2,4,1,2),(2,4,2,1),(2,4,3,2),(2,4,4,2),(2,5,1,2),(2,5,2,2),(2,5,3,2),(2,5,4,2),
(3,1,1,2),(3,1,2,2),(3,1,3,2),(3,1,4,2),(3,2,1,2),(3,2,2,2),(3,2,3,2),(3,2,4,2),
(3,3,1,2),(3,3,2,2),(3,3,3,2),(3,3,4,2),(3,4,1,2),(3,4,2,2),(3,4,3,2),(3,4,4,2),
(3,5,1,2),(3,5,2,2),(3,5,3,1),(3,5,4,2),(4,1,1,2),(4,1,2,2),(4,1,3,2),(4,1,4,2),
(4,2,1,2),(4,2,2,2),(4,2,3,2),(4,2,4,2),(4,3,1,2),(4,3,2,2),(4,3,3,2),(4,3,4,2),
(4,4,1,2),(4,4,2,2),(4,4,3,2),(4,4,4,2),(4,5,1,2),(4,5,2,2),(4,5,3,2),(4,5,4,2);
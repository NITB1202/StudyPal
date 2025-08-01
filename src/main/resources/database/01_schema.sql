CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dob DATE,
    gender VARCHAR(50),
    avatar_url VARCHAR(255)
);

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE ,
    providers VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    last_login_at TIMESTAMP,
    CONSTRAINT fk_accounts_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE teams (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    team_code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    creator_id UUID NOT NULL,
    total_members INT DEFAULT 1 NOT NULL,
    avatar_url VARCHAR(255),
    CONSTRAINT fk_teams_creator FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE teams_users (
    team_id UUID NOT NULL,
    user_id UUID NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_teams_users_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE,
    CONSTRAINT fk_teams_users_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
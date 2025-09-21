CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dob DATE,
    gender VARCHAR(50),
    avatar_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE ,
    providers VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    last_login_at TIMESTAMP,
    CONSTRAINT fk_accounts_users_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS teams (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    team_code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    creator_id UUID NOT NULL,
    total_members INT DEFAULT 1 NOT NULL,
    avatar_url VARCHAR(255),
    CONSTRAINT fk_teams_users_creator FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS teams_users (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    user_id UUID NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uq_teams_users_team_user UNIQUE (team_id, user_id),
    CONSTRAINT fk_teams_users_teams_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE,
    CONSTRAINT fk_teams_users_users_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS invitations (
    id UUID PRIMARY KEY,
    inviter_id UUID NOT NULL,
    invitee_id UUID NOT NULL,
    team_id UUID NOT NULL,
    invited_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_invitations_team_invitee UNIQUE (team_id, invitee_id),
    CONSTRAINT fk_invitations_users_inviter FOREIGN KEY (inviter_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_users_invitee FOREIGN KEY (invitee_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_teams_team FOREIGN KEY (team_id)
        REFERENCES teams (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS team_notification_settings (
    id UUID PRIMARY KEY,
    membership_id UUID NOT NULL UNIQUE,
    team_notification BOOLEAN NOT NULL,
    team_plan_reminder BOOLEAN NOT NULL,
    chat_notification BOOLEAN NOT NULL,
    CONSTRAINT fk_settings_teams_users_membership FOREIGN KEY (membership_id)
        REFERENCES teams_users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    platform VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    CONSTRAINT uq_device_tokens_user_token UNIQUE (user_id, token),
    CONSTRAINT fk_device_tokens_users_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL,
    subject VARCHAR(50),
    subject_id UUID,
    CONSTRAINT fk_notifications_users_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

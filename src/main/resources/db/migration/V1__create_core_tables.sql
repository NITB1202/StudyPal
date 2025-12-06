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
    CONSTRAINT uq_invitations_teams_invitee UNIQUE (team_id, invitee_id),
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
    image_url VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL,
    subject VARCHAR(50),
    subject_id UUID,
    CONSTRAINT fk_notifications_users_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plans (
    id UUID PRIMARY KEY,
    creator_id UUID NOT NULL,
    plan_code VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    start_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    progress FLOAT NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    team_id UUID,
    CONSTRAINT fk_plans_users_creator FOREIGN KEY (creator_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_plans_teams_team FOREIGN KEY (team_id)
        REFERENCES teams(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_histories (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL,
    image_url VARCHAR(255),
    message VARCHAR(500) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_plan_histories_plans_plan FOREIGN KEY (plan_id)
        REFERENCES plans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY,
    plan_id UUID,
    task_code VARCHAR(20) NOT NULL,
    content VARCHAR(255) NOT NULL,
    assignee_id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    priority VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    complete_date TIMESTAMP,
    parent_task_id UUID,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_tasks_plans_plan FOREIGN KEY (plan_id)
        REFERENCES plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_users_assignee FOREIGN KEY (assignee_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_parent_task FOREIGN KEY (parent_task_id)
            REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS task_recurrence_rules (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL UNIQUE,
    recurrence_start_date DATE NOT NULL,
    recurrence_end_date DATE,
    recurrence_type VARCHAR(20) NOT NULL,
    week_days VARCHAR(100),
    CONSTRAINT fk_rules_tasks_task FOREIGN KEY (task_id)
        REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task_reminders (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    remind_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_task_reminders_tasks_task FOREIGN KEY (task_id)
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT uq_task_remind_at UNIQUE (task_id, remind_at)
);

CREATE TABLE IF NOT EXISTS user_task_counters (
    id UUID NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL,
    CONSTRAINT fk_user_task_counters_users_user FOREIGN KEY (id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS team_task_counters (
    id UUID NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL,
    CONSTRAINT fk_team_task_counters_teams_team FOREIGN KEY (id)
        REFERENCES teams(id) ON DELETE CASCADE
);
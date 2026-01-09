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
    completed_at TIMESTAMP,
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

CREATE TABLE IF NOT EXISTS user_quotas (
    id UUID PRIMARY KEY,
    daily_quota BIGINT NOT NULL,
    used_quota BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_quotas_users_user FOREIGN KEY (id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    sender VARCHAR(50) NOT NULL,
    message VARCHAR(5000) NOT NULL,
    context_id UUID,
    context_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_chat_messages_users_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_usages (
    id UUID PRIMARY KEY,
    input_tokens BIGINT NOT NULL,
    output_tokens BIGINT NOT NULL,
    latency_ms BIGINT NOT NULL,
    CONSTRAINT fk_message_usages_chat_messages_message FOREIGN KEY (id)
        REFERENCES chat_messages(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message_attachments (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    url VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_attachment_message FOREIGN KEY (message_id)
        REFERENCES chat_messages(id) ON DELETE CASCADE
);

CREATE TABLE chat_idempotency (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    transaction_status VARCHAR(32) NOT NULL,
    response_message_id UUID,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_chat_idempotency_user_key
        UNIQUE (user_id, idempotency_key),
    CONSTRAINT fk_chat_idempotency_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_idempotency_response_message FOREIGN KEY (response_message_id)
        REFERENCES chat_messages(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS folders (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    bytes BIGINT NOT NULL,
    file_count INT NOT NULL,
    team_id UUID,
    is_deleted BOOLEAN NOT NULL,
    CONSTRAINT fk_folders_users_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_users_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_teams_team
        FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS files (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    extension VARCHAR(20) NOT NULL,
    folder_id UUID NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    bytes BIGINT NOT NULL,
    url VARCHAR(200) NOT NULL,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_files_folders_folder
        FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE,
    CONSTRAINT fk_files_users_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_files_users_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_usages (
    id UUID PRIMARY KEY,
    usage_used BIGINT NOT NULL,
    usage_limit BIGINT NOT NULL,
    CONSTRAINT fk_user_usages_users_user FOREIGN KEY (id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS team_usages (
    id UUID PRIMARY KEY,
    usage_used BIGINT NOT NULL,
    usage_limit BIGINT NOT NULL,
    CONSTRAINT fk_team_usages_teams_team FOREIGN KEY (id)
        REFERENCES teams(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    studied_at TIMESTAMP NOT NULL,
    duration_in_seconds BIGINT NOT NULL,
    elapsed_time_in_seconds BIGINT NOT NULL,
    CONSTRAINT fk_sessions_users_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS session_settings (
    id UUID PRIMARY KEY,
    focus_time_in_seconds BIGINT NOT NULL,
    break_time_in_seconds BIGINT NOT NULL,
    total_time_in_seconds BIGINT NOT NULL,
    enable_bg_music BOOLEAN NOT NULL,
    CONSTRAINT fk_session_settings_users_user FOREIGN KEY (id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    user_id UUID NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_messages_teams_team FOREIGN KEY (team_id)
        REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_users_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_attachments (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    url TEXT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_attachments_messages_message FOREIGN KEY (message_id)
        REFERENCES messages(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_read_status (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    user_id UUID NOT NULL,
    read_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_read_status_message FOREIGN KEY (message_id)
        REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_read_status_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_message_user_read UNIQUE (message_id, user_id)
);

CREATE TABLE IF NOT EXISTS notification_definitions (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    subject VARCHAR(20)
);
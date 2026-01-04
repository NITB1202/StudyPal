CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_users_username_trgm
    ON users(LOWER(name));

CREATE INDEX idx_teams_users_user_joined_at
    ON teams_users (user_id, joined_at);

CREATE INDEX idx_team_name_trgm
    ON teams(LOWER(name));

CREATE INDEX idx_teams_users_team_role_user
    ON teams_users (team_id, role, user_id);

CREATE INDEX idx_invitations_invitee_invited_at
    ON invitations (invitee_id, invited_at);

CREATE INDEX idx_device_tokens_user_last_updated
    ON device_tokens (user_id, last_updated DESC);


CREATE INDEX idx_notifications_user_created_at
    ON notifications (user_id, created_at);

CREATE INDEX idx_messages_team_created_at
    ON messages (team_id, created_at);

CREATE INDEX idx_message_attachments_message
    ON message_attachments (message_id);

CREATE INDEX idx_chat_idempotency_message
    ON chat_idempotency (response_message_id);

CREATE INDEX idx_chat_messages_user_created_at
    ON chat_messages (user_id, created_at);

CREATE INDEX idx_chat_message_attachments_message
    ON chat_message_attachments (message_id);

CREATE INDEX idx_files_folder_created_at
    ON files (folder_id, created_at);

CREATE INDEX idx_folders_team_created_at
    ON folders (team_id, created_at);

CREATE INDEX idx_folders_created_by_created_at
    ON folders (created_by, created_at);

CREATE INDEX idx_plans_team_start_due
    ON plans (team_id, start_date, due_date);

CREATE INDEX idx_plans_code
    ON plans (plan_code);

CREATE INDEX idx_plan_histories_plan
    ON plan_histories (plan_id);

CREATE INDEX idx_tasks_plan_start_due
    ON tasks (plan_id, start_date, due_date);

CREATE INDEX idx_tasks_assignee_start_due
    ON tasks (assignee_id, start_date, due_date);

CREATE INDEX idx_tasks_code
    ON tasks (task_code);

CREATE INDEX idx_task_recurrence_rules_task
    ON task_recurrence_rules (task_id);

CREATE INDEX idx_task_reminders_task
    ON task_reminders (task_id);
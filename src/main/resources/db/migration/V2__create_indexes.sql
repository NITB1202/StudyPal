CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Index for cursor pagination in searchUsersByName
CREATE INDEX idx_users_username_trgm
    ON users(LOWER(name));

-- Index for cursor pagination in getUserJoinedTeams and searchUserJoinedTeamsByName
CREATE INDEX idx_teams_users_user_joined_at
    ON teams_users (user_id, joined_at);

-- Index for cursor pagination in searchTeamByName
CREATE INDEX idx_team_name_trgm
    ON teams(LOWER(name));

-- Index for cursor pagination in getTeamMembers
CREATE INDEX idx_teams_users_team_role_user
    ON teams_users (team_id, role, user_id);

-- Index for cursor pagination in getInvitations
CREATE INDEX idx_invitations_invitee_invited_at
    ON invitations (invitee_id, invited_at);

-- Index for device_tokens table
CREATE INDEX idx_device_tokens_user_token
    ON device_tokens (user_id, token);

-- Index for notifications table
CREATE INDEX idx_notifications_user_created_at
    ON notifications (user_id, created_at);
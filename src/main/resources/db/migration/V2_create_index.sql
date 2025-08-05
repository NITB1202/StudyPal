CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Index for cursor pagination in searchUsersByName
CREATE INDEX idx_users_username_trgm
    ON users USING gin (LOWER(name) gin_trgm_ops);

-- Index for cursor pagination in getUserJoinedTeams and searchUserJoinedTeamsByName
CREATE INDEX idx_team_users_user_joined_at
    ON team_users (user_id, joined_at DESC);

CREATE INDEX idx_team_name_trgm
    ON teams USING gin (LOWER(name) gin_trgm_ops);

-- Index for cursor pagination in getTeamMembers
CREATE INDEX idx_team_users_team_role_user
    ON team_users (team_id, role, user_id);

-- Index for cursor pagination in getInvitations
CREATE INDEX idx_invitations_invitee_invited_at
    ON invitations (invitee_id, invited_at DESC);
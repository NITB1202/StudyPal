INSERT INTO users (id, dob, gender, name, avatar_url)
VALUES
    ('041c77e0-ae77-4005-b745-ea12dca9bec6','2003-12-04', 'UNSPECIFIED', 'Adam Lambert','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258716/041c77e0-ae77-4005-b745-ea12dca9bec6.png'),
    ('618c10ee-923f-4323-b32b-086caa534b46','1998-04-30', 'MALE', 'David Becker','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258714/618c10ee-923f-4323-b32b-086caa534b46.png'),
    ('9f5d79f2-83a2-411d-bb66-caeb640a62b0','2000-07-07', 'FEMALE', 'Harley Mavis','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258705/9f5d79f2-83a2-411d-bb66-caeb640a62b0.png');

INSERT INTO accounts (id, user_id, providers, email, hashed_password, role)
VALUES
    -- password: user123
    (
        '2e4a1e62-2ed9-4f12-9d36-1a9b1e1d1234',
        '041c77e0-ae77-4005-b745-ea12dca9bec6',
        'LOCAL',
        'user1@studypal.com',
        '$2a$10$TDjSNkaSiOa.BCfbJGdYeupsp4KdtD1qwzwKTEhoCO.dSlPK1PPUq',
        'USER'
    ),
    -- password: user123
    (
        '3e1a2b13-8fd4-40ae-81b5-1ec97a5b6789',
        '618c10ee-923f-4323-b32b-086caa534b46',
        'LOCAL',
        'user2@studypal.com',
        '$2a$10$TDjSNkaSiOa.BCfbJGdYeupsp4KdtD1qwzwKTEhoCO.dSlPK1PPUq',
        'USER'
    ),
    -- password: user123
    (
        '7a93f819-daa7-4d13-bc4f-e67f6e452aaa',
        '9f5d79f2-83a2-411d-bb66-caeb640a62b0',
        'LOCAL',
        'user3@studypal.com',
        '$2a$10$TDjSNkaSiOa.BCfbJGdYeupsp4KdtD1qwzwKTEhoCO.dSlPK1PPUq',
        'USER'
    );

INSERT INTO teams (id, name, description, team_code, created_at, creator_id, total_members, avatar_url)
VALUES
    ('111e8400-e29b-41d4-a716-446655440001', 'Biology', null, 'AQhTe1dS',
     '2024-12-01 00:00:00', '041c77e0-ae77-4005-b745-ea12dca9bec6', 2, 'https://res.cloudinary.com/drvyagz4w/image/upload/v1750258950/111e8400-e29b-41d4-a716-446655440001.jpg'),
    ('555e8400-e29b-41d4-a716-446655440006', 'Software devs', 'demo', 'fDjQA9l0',
     '2024-07-08 00:00:00', '9f5d79f2-83a2-411d-bb66-caeb640a62b0', 1, 'https://res.cloudinary.com/drvyagz4w/image/upload/v1750258952/555e8400-e29b-41d4-a716-446655440006.jpg');

INSERT INTO teams_users (id, team_id, user_id, joined_at, role)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', '111e8400-e29b-41d4-a716-446655440001', '041c77e0-ae77-4005-b745-ea12dca9bec6', '2024-12-01 00:00:00', 'OWNER'),
    ('660e8400-e29b-41d4-a716-446655440003', '111e8400-e29b-41d4-a716-446655440001', '618c10ee-923f-4323-b32b-086caa534b46', '2024-12-05 00:00:00', 'MEMBER'),
    ('770e8400-e29b-41d4-a716-446655440005', '555e8400-e29b-41d4-a716-446655440006', '9f5d79f2-83a2-411d-bb66-caeb640a62b0', '2024-07-08 00:00:00', 'OWNER');

INSERT INTO invitations (id, inviter_id, invitee_id, team_id, invited_at)
VALUES
    ('75d8831a-7324-436d-aab0-edbec38e38bb','9f5d79f2-83a2-411d-bb66-caeb640a62b0', '041c77e0-ae77-4005-b745-ea12dca9bec6', '555e8400-e29b-41d4-a716-446655440006', '2025-08-03 10:30:00');

INSERT INTO team_notification_settings (id, membership_id, team_notification, team_plan_reminder, chat_notification)
VALUES
    ('6fbeb41d-74f4-4e1b-be73-9ff503800e5d', '550e8400-e29b-41d4-a716-446655440000', TRUE, TRUE, TRUE),
    ('23188d8a-ebc9-41ab-94a0-7d23bcc17305', '660e8400-e29b-41d4-a716-446655440003', TRUE, TRUE, TRUE),
    ('edc72285-d3b1-4ad9-95f1-47399d471385', '770e8400-e29b-41d4-a716-446655440005', TRUE, TRUE, TRUE);

INSERT INTO device_tokens (id, user_id, platform, token, last_updated)
VALUES
    ('abbb2730-0d5f-4ab0-9154-05e8d24258fb', '041c77e0-ae77-4005-b745-ea12dca9bec6', 'ANDROID', 'fcm_android_3a9f8x7n2k0lm5dqpw', '2025-09-15 00:00:00'),
    ('ac2d2a0c-b654-4fd6-b2cb-cbbb6c92872f', '618c10ee-923f-4323-b32b-086caa534b46', 'ANDROID', 'fcm_android_9z7h6x4b3p1k2w8nq0', '2025-09-15 00:00:00'),
    ('e567f27a-f5c5-4597-8837-752e20e47611', '9f5d79f2-83a2-411d-bb66-caeb640a62b0', 'ANDROID', 'fcm_android_0q1w2e3r4t5y6u7i8o9p','2025-09-15 00:00:00');

INSERT INTO notifications (id, user_id, image_url, title, content, created_at, is_read, subject, subject_id)
VALUES
    (
        '1e84fc88-1a6d-4e99-9022-3a0191a5c1f1',
        '041c77e0-ae77-4005-b745-ea12dca9bec6',
        'https://res.cloudinary.com/drvyagz4w/image/upload/v1750258714/618c10ee-923f-4323-b32b-086caa534b46.png',
        'Team deleted',
        'David Becker deleted Team01.',
        '2025-09-20 10:30:00',
        FALSE,
        'TEAM',
        null
    ),
    (
        'a2bdf7e9-45c7-4ec0-b53e-8e9a5d9a9fbc',
        '041c77e0-ae77-4005-b745-ea12dca9bec6',
        'https://res.cloudinary.com/drvyagz4w/image/upload/v1750258714/618c10ee-923f-4323-b32b-086caa534b46.png',
        'New team member',
        'David Becker joined Biology.',
        '2025-09-21 08:00:00',
        FALSE,
        'TEAM',
        '111e8400-e29b-41d4-a716-446655440001'
    );

INSERT INTO user_task_counters (id, counter)
VALUES
    ('041c77e0-ae77-4005-b745-ea12dca9bec6', 0),
    ('618c10ee-923f-4323-b32b-086caa534b46', 0),
    ('9f5d79f2-83a2-411d-bb66-caeb640a62b0', 0);

INSERT INTO team_task_counters (id, counter)
VALUES
    ('111e8400-e29b-41d4-a716-446655440001', 0),
    ('555e8400-e29b-41d4-a716-446655440006', 0);

INSERT INTO user_quotas (id, daily_quota, used_quota, created_at, updated_at)
VALUES
    ('041c77e0-ae77-4005-b745-ea12dca9bec6', 15000, 0, '2024-12-01 00:00:00', '2024-12-01 00:00:00'),
    ('618c10ee-923f-4323-b32b-086caa534b46', 15000, 0, '2024-12-01 00:00:00', '2024-12-01 00:00:00'),
    ('9f5d79f2-83a2-411d-bb66-caeb640a62b0', 15000, 0, '2024-12-01 00:00:00', '2024-12-01 00:00:00');
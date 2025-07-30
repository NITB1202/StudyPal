INSERT INTO users (id, dob, gender, name, avatar_url) VALUES
('041c77e0-ae77-4005-b745-ea12dca9bec6','2003-12-04', 'UNSPECIFIED', 'Adam Lambert','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258716/041c77e0-ae77-4005-b745-ea12dca9bec6.png'),
('618c10ee-923f-4323-b32b-086caa534b46','1998-04-30', 'MALE', 'David Becker','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258714/618c10ee-923f-4323-b32b-086caa534b46.png'),
('9f5d79f2-83a2-411d-bb66-caeb640a62b0','2000-07-07', 'FEMALE', 'Harley Mavis','https://res.cloudinary.com/drvyagz4w/image/upload/v1750258705/9f5d79f2-83a2-411d-bb66-caeb640a62b0.png');

INSERT INTO accounts (id, user_id, provider, provider_id, email, hashed_password, role)
VALUES
    -- password: admin123
    (
        '2e4a1e62-2ed9-4f12-9d36-1a9b1e1d1234',
        '041c77e0-ae77-4005-b745-ea12dca9bec6',
        'LOCAL',
        NULL,
        'user1@studypal.com',
        '$2a$10$BKfmTWy1Sq4NWfdXHa.08O1dMZke1Kl72tPWCJ4FGNIjFz/CxxbDu',
        'USER'
    ),
    -- password: user1234
    (
        '3e1a2b13-8fd4-40ae-81b5-1ec97a5b6789',
        '618c10ee-923f-4323-b32b-086caa534b46',
        'LOCAL',
        NULL,
        'user2@studypal.com',
        '$2a$10$zpygZ72GKBRwd8PNiHcRMO5muGxZr/YfJ.V4Y2sCfai7CP76wpxHC',
        'USER'
    ),
    -- password: system123
    (
        '7a93f819-daa7-4d13-bc4f-e67f6e452aaa',
        '9f5d79f2-83a2-411d-bb66-caeb640a62b0',
        'LOCAL',
        NULL,
        'user3@studypal.com',
        '$2a$10$/j.77sEIl8aJBjOSj1G6.erGDyAE19Z2BsRcPABkhgb8GNfiwp6cO',
        'USER'
    );
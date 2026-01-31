-- Normalize stored enum values to lowercase so they match native enum definitions.
UPDATE app_user SET role = LOWER(role);
UPDATE membership SET type = LOWER(type);
UPDATE class_session SET time_slot = LOWER(time_slot);
UPDATE reservation SET status = LOWER(status);
UPDATE record SET type = LOWER(type);

ALTER TABLE app_user
    MODIFY COLUMN role ENUM('admin','coach','member') NOT NULL;

ALTER TABLE membership
    MODIFY COLUMN type ENUM('period','count') NOT NULL;

ALTER TABLE class_session
    MODIFY COLUMN time_slot ENUM('slot_09_00','slot_10_30','slot_17_30','slot_19_00','slot_20_30') NOT NULL;

ALTER TABLE reservation
    MODIFY COLUMN status ENUM('booked','waitlisted','cancelled') NOT NULL;

ALTER TABLE record
    MODIFY COLUMN type ENUM('time','rm','round') NOT NULL;

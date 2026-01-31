-- Align enum storage with Java enum names (uppercase).
UPDATE app_user SET role = UPPER(role);
UPDATE membership SET type = UPPER(type);
UPDATE class_session SET time_slot = UPPER(time_slot);
UPDATE reservation SET status = UPPER(status);
UPDATE record SET type = UPPER(type);

ALTER TABLE app_user
    MODIFY COLUMN role ENUM('ADMIN','COACH','MEMBER') NOT NULL;

ALTER TABLE membership
    MODIFY COLUMN type ENUM('PERIOD','COUNT') NOT NULL;

ALTER TABLE class_session
    MODIFY COLUMN time_slot ENUM('SLOT_09_00','SLOT_10_30','SLOT_17_30','SLOT_19_00','SLOT_20_30') NOT NULL;

ALTER TABLE reservation
    MODIFY COLUMN status ENUM('BOOKED','WAITLISTED','CANCELLED') NOT NULL;

ALTER TABLE record
    MODIFY COLUMN type ENUM('TIME','RM','ROUND') NOT NULL;

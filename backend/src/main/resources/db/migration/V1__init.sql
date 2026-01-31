CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE membership (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    start_date DATE,
    end_date DATE,
    remaining_count INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE class_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    capacity INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uq_session_date_slot (session_date, time_slot)
);

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    waitlist_position INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uq_res_user_session (user_id, session_id),
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_res_session FOREIGN KEY (session_id) REFERENCES class_session(id)
);

CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    att_date DATE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uq_att_user_date (user_id, att_date),
    CONSTRAINT fk_att_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE wod (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wod_date DATE NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_wod_user FOREIGN KEY (created_by) REFERENCES app_user(id)
);

CREATE TABLE record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wod_id BIGINT,
    type VARCHAR(20) NOT NULL,
    record_value VARCHAR(100) NOT NULL,
    record_date DATE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_record_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_record_wod FOREIGN KEY (wod_id) REFERENCES wod(id)
);

CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_notice_user FOREIGN KEY (created_by) REFERENCES app_user(id)
);

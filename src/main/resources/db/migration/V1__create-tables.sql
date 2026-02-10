CREATE TABLE users (
    id	 BIGSERIAL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE secrets (
    id	 BIGSERIAL,
    issuer	 TEXT NOT NULL,
    secret	 TEXT NOT NULL,
    users_id BIGINT NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE devices (
    id	 TEXT,
    name TEXT NOT NULL,
    admin BOOLEAN NOT NULL,
    users_id BIGINT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE users ADD UNIQUE (username);
ALTER TABLE secrets ADD CONSTRAINT secrets_fk1 FOREIGN KEY (users_id) REFERENCES users(id);
ALTER TABLE devices ADD CONSTRAINT devices_fk1 FOREIGN KEY (users_id) REFERENCES users(id);
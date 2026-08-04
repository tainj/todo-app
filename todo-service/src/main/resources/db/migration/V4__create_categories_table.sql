CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT REFERENCES users(id)
);

INSERT INTO categories (name, user_id) VALUES
    ('Work', NULL),
    ('Personal', NULL),
    ('Shopping', NULL);
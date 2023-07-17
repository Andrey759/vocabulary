
CREATE TABLE user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50),
    enabled BOOLEAN
);

CREATE TABLE card (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    word VARCHAR(50),
    sentence VARCHAR(1000),
    sentence_html VARCHAR(1000),
    translation_html VARCHAR(1000),
    explanation_html VARCHAR(1000),
    status VARCHAR(50),
    ready_at TIMESTAMP(6)
)

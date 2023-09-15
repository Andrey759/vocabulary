
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50),
    enabled BOOLEAN,
    voice_enabled BOOLEAN,
    voice_card VARCHAR(50),
    voice_chat_left VARCHAR(50),
    voice_chat_right VARCHAR(50),
    voice_rate DOUBLE,
    voice_volume DOUBLE
);

CREATE TABLE cards (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    word VARCHAR(50),
    response VARCHAR,
    sentence VARCHAR(1000),
    sentence_html VARCHAR(1000),
    translation_html VARCHAR(1000),
    explanation_html VARCHAR(1000),
    status VARCHAR(50),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    ready_at TIMESTAMP(6),
    repeat_order INTEGER
);

CREATE TABLE messages (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    owner VARCHAR(50),
    mark INT,
    message VARCHAR(1000),
    response VARCHAR,
    corrected VARCHAR(1000),
    corrected_html VARCHAR(1000),
    perfect VARCHAR(1000),
    created_at TIMESTAMP(6)
);

CREATE TABLE telegram_messages (
    id BIGINT PRIMARY KEY,
    chat_id BIGINT,
    message_role VARCHAR(50),
    text VARCHAR
);


CREATE TABLE user (
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

CREATE TABLE card (
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

CREATE TABLE message (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    owner VARCHAR(50),
    mark INT,
    response VARCHAR,
    corrected VARCHAR(1000),
    corrected_html VARCHAR(1000),
    perfect VARCHAR(1000),
    created_at TIMESTAMP(6)
);

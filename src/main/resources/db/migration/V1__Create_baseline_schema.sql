
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_profile_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Quando a aplicação solicita os desafios de um usuário (SELECT * FROM challenges WHERE author_id = 'UUID'), o banco de dados precisa ler cada linha da tabela do início ao fim para encontrar as correspondências. A complexidade de tempo é O(N), onde N é o número total de registros. Em tabelas com milhões de linhas, isso causa degradação severa de performance.

-- Com Índice (Index Scan): O banco de dados percorre a B-Tree, que mantém as chaves ordenadas. A busca ocorre em complexidade de tempo O(log N).

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

CREATE TABLE challenges (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    platform_origin VARCHAR(100),
    source_code TEXT NOT NULL,
    time_complexity VARCHAR(50),
    space_complexity VARCHAR(50),
    ai_autonomy_index SMALLINT CHECK (ai_autonomy_index >= 1 AND ai_autonomy_index <= 5),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_challenges_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_challenges_author_id ON challenges(author_id);
CREATE INDEX idx_challenges_is_public ON challenges(is_public) WHERE is_public = TRUE;

CREATE TABLE snippets (
    id UUID PRIMARY KEY,
    challenge_id UUID NOT NULL,
    code TEXT NOT NULL,
    description TEXT,  
    concept_category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snippets_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE
);

CREATE INDEX idx_snippets_challenge_id ON snippets(challenge_id);
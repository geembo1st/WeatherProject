CREATE TABLE Sessions (
                          id UUID PRIMARY KEY,
                          user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
                          expires_at TIMESTAMP NOT NULL
);
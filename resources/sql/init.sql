-- Tables for initialising databases

-- :name create-wordtrack-table
-- :command :execute
-- :result :raw
-- :doc Create ticket_events table
CREATE TABLE IF NOT EXISTS wordtrack (
    -- This is dirty, and should really be in separate tables, but w/e
    word TEXT NOT NULL,
    t TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )

-- CREATE INDEX IF NOT EXISTS wordtrack_word
-- ON wordtrack (word);
-- CREATE INDEX IF NOT EXISTS wordtrack_t
-- ON wordtrack (t);

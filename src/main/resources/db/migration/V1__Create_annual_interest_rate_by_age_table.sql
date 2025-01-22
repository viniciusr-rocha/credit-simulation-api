CREATE TABLE annual_interest_rate_by_age
(
    id            SERIAL PRIMARY KEY,
    min_age       INT           NOT NULL,
    max_age       INT,
    interest_rate DECIMAL(5, 2) NOT NULL,
    created_at    TIMESTAMPTZ
);

CREATE INDEX idx_min_age ON annual_interest_rate_by_age (min_age);
CREATE INDEX idx_max_age ON annual_interest_rate_by_age (max_age);

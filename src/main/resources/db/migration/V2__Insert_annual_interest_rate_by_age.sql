INSERT INTO annual_interest_rate_by_age (min_age, max_age, interest_rate, created_at)
VALUES (0, 25, 5, CURRENT_TIMESTAMP),  -- Até 25 anos: 5% ao ano
       (26, 40, 3, CURRENT_TIMESTAMP), -- De 26 a 40 anos: 3% ao ano
       (41, 60, 2, CURRENT_TIMESTAMP), -- De 41 a 60 anos: 2% ao ano
       (61, NULL, 5, CURRENT_TIMESTAMP); -- Acima de 60 anos: 4% ao ano

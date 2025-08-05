ALTER TABLE schedule DROP COLUMN status;

ALTER TABLE selectdate add column status VARCHAR(50) NOT NULL;
ALTER TABLE Schedule DROP repeat_type;
ALTER TABLE Schedule DROP repeat_cycle;
ALTER TABLE Schedule ADD repeat_yn BOOLEAN NOT NULL;
ALTER TABLE Schedule ADD selected_dates JSON NOT NULL;
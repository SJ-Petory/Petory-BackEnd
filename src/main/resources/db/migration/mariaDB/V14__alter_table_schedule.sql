ALTER TABLE schedule DROP repeat_type;
ALTER TABLE schedule DROP repeat_cycle;
ALTER TABLE schedule ADD repeat_yn BOOLEAN NOT NULL;
ALTER TABLE schedule ADD selected_dates JSON NOT NULL;
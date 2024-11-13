ALTER TABLE schedule CHANGE repeat_type repeat_yn BOOLEAN;
ALTER TABLE schedule DROP repeat_cycle;
ALTER TABLE schedule ADD selected_dates JSON NOT NULL;
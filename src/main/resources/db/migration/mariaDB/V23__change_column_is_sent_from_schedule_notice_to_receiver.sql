ALTER TABLE ScheduleNotification DROP COLUMN is_sent;

ALTER TABLE ScheduleNotificationReceiver ADD COLUMN is_sent BOOLEAN NOT NULL;
-- First add the column as nullable
ALTER TABLE employee_schedules ADD COLUMN IF NOT EXISTS title VARCHAR(255);

-- Then update existing records with a default title
UPDATE employee_schedules SET title = 'Schedule - ' || username WHERE title IS NULL;

-- Finally, set the column to NOT NULL after all records have a value
ALTER TABLE employee_schedules ALTER COLUMN title SET NOT NULL;

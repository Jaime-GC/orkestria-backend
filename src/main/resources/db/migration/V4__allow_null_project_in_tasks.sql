-- Liquibase Migration Script to allow NULL project_id in tasks table
ALTER TABLE tasks ALTER COLUMN project_id DROP NOT NULL;

-- Create resource_groups table if it doesn't exist
CREATE TABLE IF NOT EXISTS resource_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    is_reservable BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (parent_id) REFERENCES resource_groups(id)
);

-- Create temporary table to check if resource_items exists
CREATE OR REPLACE FUNCTION migrate_resource_items() RETURNS void AS $$
BEGIN
    -- Check if resource_items table exists
    IF EXISTS (SELECT FROM information_schema.tables 
               WHERE table_schema = 'public' 
               AND table_name = 'resource_items') THEN
        
        -- Create a temporary table for resource items data
        CREATE TEMPORARY TABLE temp_resource_items AS 
        SELECT id, name, group_id FROM resource_items;
        
        -- Insert into resource_groups with is_reservable=true
        INSERT INTO resource_groups (name, parent_id, is_reservable)
        SELECT name, group_id, TRUE
        FROM temp_resource_items;
        
        -- Handle space_reservations if they exist
        IF EXISTS (SELECT FROM information_schema.tables 
                  WHERE table_schema = 'public' 
                  AND table_name = 'space_reservations') THEN
            
            -- Check if the column exists
            IF EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_schema = 'public' 
                      AND table_name = 'space_reservations' 
                      AND column_name = 'resource_item_id') THEN
                
                -- Create mapping table
                CREATE TEMPORARY TABLE id_mapping AS
                SELECT old.id AS old_id, new.id AS new_id
                FROM temp_resource_items old
                JOIN resource_groups new ON 
                    new.name = old.name AND 
                    new.parent_id = old.group_id AND
                    new.is_reservable = TRUE;
                
                -- Add new column
                ALTER TABLE space_reservations
                ADD COLUMN IF NOT EXISTS resource_group_id BIGINT;
                
                -- Update references
                UPDATE space_reservations sr
                SET resource_group_id = (
                    SELECT new_id 
                    FROM id_mapping 
                    WHERE old_id = sr.resource_item_id
                );
                
                -- Add constraint if it doesn't exist
                IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                              WHERE constraint_name = 'fk_space_reservations_resource_group') THEN
                    ALTER TABLE space_reservations
                    ADD CONSTRAINT fk_space_reservations_resource_group
                    FOREIGN KEY (resource_group_id) REFERENCES resource_groups(id);
                END IF;
                
                -- Make not nullable
                ALTER TABLE space_reservations
                ALTER COLUMN resource_group_id SET NOT NULL;
                
                -- Drop old constraint and column
                ALTER TABLE space_reservations 
                DROP CONSTRAINT IF EXISTS fk_resource_item;
                
                ALTER TABLE space_reservations
                DROP COLUMN IF EXISTS resource_item_id;
                
                -- Clean up temp tables
                DROP TABLE IF EXISTS id_mapping;
            END IF;
        END IF;
        
        -- Drop temporary tables
        DROP TABLE IF EXISTS temp_resource_items;
        
        -- Drop old table
        DROP TABLE IF EXISTS resource_items;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Execute the migration function
SELECT migrate_resource_items();

-- Drop the function after use
DROP FUNCTION IF EXISTS migrate_resource_items();

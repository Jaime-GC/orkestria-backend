CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    start_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETE'))
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    project_id BIGINT, 
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('URGENT', 'RECURRING', 'OTHER')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('TODO', 'DOING', 'BLOCKED', 'DONE')),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Add users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENT'
);

-- Resource module tables
CREATE TABLE resource_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES resource_groups(id)
);

CREATE TABLE resource_items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    group_id BIGINT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES resource_groups(id)
);

CREATE TABLE employee_schedules (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL
);

CREATE TABLE space_reservations (
    id SERIAL PRIMARY KEY,
    resource_item_id BIGINT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    reserved_by VARCHAR(50) NOT NULL,
    FOREIGN KEY (resource_item_id) REFERENCES resource_items(id)
);
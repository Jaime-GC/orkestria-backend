CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETE'))
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
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
    email VARCHAR(255) NOT NULL
);

-- Join table for embedded enum roles
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
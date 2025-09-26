-- Initial database setup for TRPGManager

-- Create extensions if they don't exist
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create application user if not exists (for production use)
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'trpg_user') THEN
      CREATE USER trpg_user WITH PASSWORD 'trpg_password';
   END IF;
END
$$;

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE trpg_db TO trpg_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO trpg_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO trpg_user;
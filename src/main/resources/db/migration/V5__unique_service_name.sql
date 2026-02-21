ALTER TABLE services
ADD CONSTRAINT unique_service_name UNIQUE (name);
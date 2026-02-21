-- services table (matches ServiceEntity)
CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    duration_minutes INTEGER NOT NULL
);

-- appointments table (matches AppointmentEntity)
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    customer_email TEXT NOT NULL,
    service_id BIGINT NOT NULL REFERENCES services(id),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL
);

-- Optional but smart: index for your "my appointments" query
CREATE INDEX IF NOT EXISTS idx_appointments_customer_start_desc
    ON appointments (customer_email, start_time DESC);

-- Basic integrity: end must be after start
ALTER TABLE appointments
    ADD CONSTRAINT appointments_end_after_start
    CHECK (end_time > start_time);
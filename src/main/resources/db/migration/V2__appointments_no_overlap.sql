-- Needed so we can use "=" on BIGINT inside a GiST exclusion constraint
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Prevent overlapping BOOKED appointments per service
ALTER TABLE appointments
ADD CONSTRAINT appointments_no_overlap_booked
EXCLUDE USING gist (
  service_id WITH =,
  tstzrange(start_time, end_time, '[)') WITH &&
)
WHERE (status = 'BOOKED');
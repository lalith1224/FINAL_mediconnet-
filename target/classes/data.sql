-- Test data for MediConnect
-- This file is automatically executed by Spring Boot after schema creation

-- Insert test users for doctors
INSERT INTO users (id, first_name, last_name, email, password, role, created_at, updated_at) 
VALUES (
    gen_random_uuid(),
    'John',
    'Smith',
    'doctor@test.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'DOCTOR',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (id, first_name, last_name, email, password, role, created_at, updated_at) 
VALUES (
    gen_random_uuid(),
    'Sarah',
    'Johnson',
    'doctor2@test.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'DOCTOR',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (id, first_name, last_name, email, password, role, created_at, updated_at) 
VALUES (
    gen_random_uuid(),
    'Michael',
    'Brown',
    'doctor3@test.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'DOCTOR',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Insert corresponding doctor records
INSERT INTO doctors (id, user_id, license_number, specialization, experience, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.id,
    'MD123456',
    'Cardiology',
    10,
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'doctor@test.com' 
AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

INSERT INTO doctors (id, user_id, license_number, specialization, experience, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.id,
    'MD789012',
    'Pediatrics',
    8,
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'doctor2@test.com' 
AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

INSERT INTO doctors (id, user_id, license_number, specialization, experience, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.id,
    'MD345678',
    'Dermatology',
    12,
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'doctor3@test.com' 
AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

-- Insert a test patient
INSERT INTO users (id, first_name, last_name, email, password, role, created_at, updated_at) 
VALUES (
    gen_random_uuid(),
    'Alice',
    'Wilson',
    'patient@test.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'PATIENT',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO patients (id, user_id, date_of_birth, gender, phone, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.id,
    '1990-05-15',
    'Female',
    '+1234567890',
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'patient@test.com' 
AND NOT EXISTS (SELECT 1 FROM patients p WHERE p.user_id = u.id);
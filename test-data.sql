-- Test data for MediConnect
-- Insert a test user for doctor
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
);

-- Insert corresponding doctor record
INSERT INTO doctors (id, user_id, license_number, specialization, experience, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'doctor@test.com'),
    'MD123456',
    'Cardiology',
    10,
    NOW(),
    NOW()
);

-- Insert another test doctor
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
);

INSERT INTO doctors (id, user_id, license_number, specialization, experience, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'doctor2@test.com'),
    'MD789012',
    'Pediatrics',
    8,
    NOW(),
    NOW()
);
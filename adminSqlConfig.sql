-- 1. Tạo quyền ROLE_ADMIN nếu chưa có
IF NOT EXISTS (SELECT 1 FROM jhi_authority WHERE name = 'ROLE_ADMIN')
BEGIN
    INSERT INTO jhi_authority (name) VALUES ('ROLE_ADMIN');
END;

-- 2. Tạo user admin (id = 9999)
IF NOT EXISTS (SELECT 1 FROM jhi_user WHERE login = 'admin')
BEGIN
    INSERT INTO jhi_user (
        id,
        login,
        password_hash,
        first_name,
        last_name,
        email,
        activated,
        lang_key,
        created_by,
        created_date
    ) VALUES (
        9999,
        'admin',
        '$2a$10$96a0yC2oqNeklS8J6HPjg.qCnlaLRCCx97wcwwhD6KBxBhi8no3Ui', -- Mật khẩu: admin
        'Admin',
        'System',
        'admin@example.com',
        1,
        'en',
        'system',
        GETDATE()
    );
END;

-- 3. Gán quyền ROLE_ADMIN cho user admin
IF NOT EXISTS (
    SELECT 1 FROM jhi_user_authority
    WHERE user_id = 9999 AND authority_name = 'ROLE_ADMIN'
)
BEGIN
    INSERT INTO jhi_user_authority (user_id, authority_name)
    VALUES (9999, 'ROLE_ADMIN');
END;

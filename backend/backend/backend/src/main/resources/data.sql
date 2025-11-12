-- Inserir admin padrão (senha: admin123)
INSERT INTO usuario (id, nome, email, senha, role, ativo)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Administrador', 'admin@lourenco.com', '$2a$10$rO5D8Ij7MbqYx8U6JEpxJ.Ql6XoZrMHqE.gN0H3kP9Jw6rY8E2ZKe', 'ADMIN', true)
ON CONFLICT (email) DO NOTHING;

-- Inserir categorias
INSERT INTO categoria (id, nome)
VALUES 
    ('c1111111-1111-1111-1111-111111111111', 'Cupcakes'),
    ('c2222222-2222-2222-2222-222222222222', 'Bolos'),
    ('c3333333-3333-3333-3333-333333333333', 'Tortas'),
    ('c4444444-4444-4444-4444-444444444444', 'Macarons'),
    ('c5555555-5555-5555-5555-555555555555', 'Donuts')
ON CONFLICT DO NOTHING;
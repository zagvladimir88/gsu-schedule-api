CREATE TABLE departments (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE,
                             created_at TIMESTAMP DEFAULT NOW(),
                             updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для корпусов
CREATE TABLE buildings (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           created_at TIMESTAMP DEFAULT NOW(),
                           updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для кабинетов
CREATE TABLE classrooms (
                            id SERIAL PRIMARY KEY,
                            building_id INTEGER NOT NULL REFERENCES buildings(id) ON DELETE CASCADE,
                            number VARCHAR(50) NOT NULL,
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW(),
                            UNIQUE (building_id, number)
);

-- Создание таблицы для преподавателей
CREATE TABLE teachers (
                          id SERIAL PRIMARY KEY,
                          full_name VARCHAR(255) NOT NULL,
                          department_id INTEGER NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для предметов
CREATE TABLE subjects (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для учебных групп
CREATE TABLE groups (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        course INTEGER NOT NULL CHECK (course BETWEEN 1 AND 6),
                        created_at TIMESTAMP DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для пользователей (админы и менеджеры)
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL CHECK (role IN ('admin', 'manager')),
                       created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание таблицы для подписок чатов
CREATE TABLE subscriptions (
                               id SERIAL PRIMARY KEY,
                               telegram_chat_id BIGINT NOT NULL,
                               group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
                               created_at TIMESTAMP DEFAULT NOW(),
                               UNIQUE (telegram_chat_id, group_id)
);

-- Создание таблицы расписания
CREATE TABLE schedules (
                           id SERIAL PRIMARY KEY,
                           group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
                           subject_id INTEGER NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
                           teacher_id INTEGER NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
                           classroom_id INTEGER NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
                           date DATE NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           created_at TIMESTAMP DEFAULT NOW(),
                           updated_at TIMESTAMP DEFAULT NOW()
);

-- Создание индексов для оптимизации
CREATE INDEX idx_schedules_date ON schedules(date);
CREATE INDEX idx_schedules_group_id ON schedules(group_id);
CREATE INDEX idx_subscriptions_chat_id ON subscriptions(telegram_chat_id);
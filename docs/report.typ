#set text(16pt, lang: "ru")
#set align(center)

#v(1cm)

*Федеральное государственное автономное
образовательное учреждение высшего образования "Национальный
исследовательский университет ИТМО"*

\

Дисциплина \ "Информационная безопасность"
#v(2.5cm)

*Лабораторная работа №1*\ Разработка защищенного REST API с интеграцией в CI/CD

#v(4cm)

#set text(16pt)
#set align(left)

#table(
  columns: 2,
  stroke: none,
  [*Выполнил:*], [Малышев Никита Александрович],
  [*Группа:*], [P3412],
)

#set align(center)

#v(5cm)
#datetime.today().year() год

#pagebreak()

#set text(12pt)
#set align(left)

= Ссылка на публичный репозиторий с кодом проекта

https://github.com/nktmlshv/infosec-lab1

= О проекте

- Spring Boot API
- JWT-аутентификация
- PostgreSQL
- защищённый набор эндпоинтов для работы с данными и профилем
- CI/CD с SAST и SCA

= API

== 1. Авторизация

- `POST /auth/login`
- вход: `username`, `password`
- ответ: JWT token + expiration

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "StrongAdminPassword123!"
  }'
```

== 2. Получение данных

- `GET /api/data`
- требует JWT
- возвращает список постов

```bash
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

== 3. Профиль текущего пользователя

- `GET /api/profile`
- требует JWT
- возвращает username + message

```bash
curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

= Меры защиты

== Аутентификация и авторизация

- `SecurityConfig`
- `permitAll()` только для `/auth/login` и `/error`
- все остальные маршруты: `authenticated()`
- `BCryptPasswordEncoder`
- JWT filter: проверка `Authorization: Bearer ...`
- `JwtAuthenticationFilter`: в каждом запросе
  - извлекает токен
  - валидирует подпись и срок жизни
  - загружает пользователя по username
  - кладёт authentication в `SecurityContextHolder`
- `JwtUtil`
  - обязательная проверка `jwt.secret`
  - минимальная длина секрета: 32 символа
  - `HS256`
  - expiration в ms

== Защита от SQL Injection

- основная работа с БД через Spring Data JPA / Hibernate
- репозитории без raw SQL
- все запросы идут через параметризованные JPA-методы
- `UserRepository.findByUsername(...)`
- `PostRepository.findAllByOrderByCreatedAtDesc()`
- нет конкатенации пользовательского ввода в SQL строку

== Защита от XSS

- `HtmlSanitizer`
- `StringEscapeUtils.escapeHtml4(...)`
- HTML-экранирование перед возвратом данных в ответ
- применено к username, title, content, author

= Скриншоты отчётов

#figure(
  image("media/spotbugsXml.png", width: 100%),
  caption: [SpotBugs report]
)

#figure(
  image("media/dependency-check.png", width: 100%),
  caption: [Dependency Check report]
)

= Ссылка на последний успешный запуск pipeline

https://github.com/nktmlshv/infosec-lab1/actions/runs/36674824973
# ContestPulse

ContestPulse is a competitive programming contest tracking and notification platform built with Java and Spring Boot.

It collects upcoming coding contests, stores them in a centralized database, and provides users with configurable reminders through notification channels.

## Features

- Track upcoming competitive programming contests
- Codeforces contest integration
- Automatic contest synchronization
- User management
- Configurable notification preferences
- Email notifications
- Telegram notifications
- Automated contest reminders
- Duplicate notification prevention
- Notification history
- User-specific timezone support
- REST APIs for contest and user management
- Centralized exception handling
- Request validation
- Persistent MySQL database

## Architecture

```text
                    Codeforces API
                          │
                          ▼
                ContestPulse Backend
                  Spring Boot REST API
                          │
             ┌────────────┼────────────┐
             ▼            ▼            ▼
          Contest        User      Notification
          Service       Service       Service
             │            │            │
             └────────────┼────────────┘
                          ▼
                         MySQL
                          │
                          ▼
                  Notification Logs
                          │
                  ┌───────┴────────┐
                  ▼                ▼
                Email           Telegram

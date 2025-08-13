# 📚 StudyPal - Backend

## 📝 Overview
Backend for a mobile application designed to support **self-study**.  
It provides a full set of features for personal and group learning management, document storage, real-time chat, notifications, and payment integration.

**Architecture**
- Monolith
- Multi-Modular

**Tech Stack**
- **Backend Framework:** Java (Spring Boot, Spring Security)
- **Database:** PostgreSQL
- **Real-time Communication:** WebSocket
- **API Documentation:** OpenAPI
- **File Storage:** Cloudinary
- **Database Migration:** Flyway
- **Caching:** Caffeine
- **CI/CD:** GitHub Actions
- **Containerization:** Docker
- **Event Handling:** Spring ApplicationEvent

---

## 🎯 Features

- **Auth Service**: Login, registration, logout, password recovery, Google login  
- **User Service**: Manage personal information, search for other users  
- **Team Service**: Manage study groups, member management, role assignments  
- **Plan Service**: Manage study plans, member contributions, statistics, priority classification, AI task suggestions  
- **Session Service**: Create and store study sessions, track study time  
- **Document Service**: Store and categorize documents, manage editing permissions in teams  
- **Chat Service**: Group chat, group calls  
- **Notification Service**: Plan notifications, group invitations, push notifications to devices  
- **Payment Service**: Payment system to expand personal storage capacity

---

## 🛠 Run in Development

### 1. Clone Repository
```bash
git clone https://github.com/NITB1202/StudyPal.git
cd StudyPal
```

### 2. Create `.env`
Copy the `.env.example` file and rename it to `.env`, then fill in the configuration values.

For local development:
```env
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost
```

Run:
```bash
cp .env.example .env
```

---

### 3. Start Development Services
```bash
docker compose -f docker-compose.dev.yml up -d
```

---

### 4. Run the Application
- Open the project in an IDE (IntelliJ IDEA, VS Code, etc.)
- Run the main application class
- Access API documentation at: [http://localhost:8080/swagger](http://localhost:8080/swagger)

---

## 🌐 Staging
- API Docs: [103.211.201.112:8080/swagger](http://103.211.201.112:8080/swagger)  
- Staging deployment configuration is managed internally.

---

## 📄 License
Internal development project – license to be defined.

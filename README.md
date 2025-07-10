💬 AI Chat App (Gemini-powered)

An interactive, real-time AI chat application built with **Spring Boot**, **WebSockets**, and **Thymeleaf**, integrating **Gemini AI** for dynamic responses. This project supports multi-session chat, role-based access (user/admin), and includes advanced features like exporting chat history to PDF/JSON.

---

✨ Features

- ✅ User registration and login with Spring Security
- 🧠 AI-powered chat via Gemini (Google AI)
- 🔁 Real-time streaming response with WebSockets (SockJS + STOMP)
- 📂 Chat session management (create, rename, delete)
- 📜 Full chat history with timestamps
- 📤 Export chat history as **JSON** or **PDF**
- 🛠️ Admin Dashboard:
  - View/delete all users and sessions
  - View all chat entries
- 🗑️ Individual chat message deletion
- 🔐 Role-based authentication (USER / ADMIN)

---

🔧 Tech Stack

| Layer        | Technology               |
|--------------|--------------------------|
| Backend      | Spring Boot, Spring MVC  |
| Security     | Spring Security, BCrypt  |
| Frontend     | HTML, Thymeleaf, JS      |
| WebSocket    | SockJS + STOMP           |
| Database     | MySQL / H2 (JPA, Hibernate) |
| PDF Export   | iText / OpenPDF          |
| JSON Export  | Jackson                  |
| AI Model     | Gemini API               |

---

🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/Princee2245/AIChatApp.git
cd AIChatApp
```

### 2. Set up database

Update `application.properties` for your MySQL or H2 setup.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aichat
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Then open:  
`http://localhost:8080`

---

🧑‍💻 Default Roles

| Role  | Access                          |
|-------|---------------------------------|
| USER  | Chat, view own sessions         |
| ADMIN | View/delete all sessions, users |

You can manually assign roles in the database (`user.role` field).

---

## 📂 Folder Structure

```plaintext
├── controller
├── entity
├── repository
├── service
├── config
├── templates/
│   ├── chat.html
│   ├── admin/
│       ├── dashboard.html
│       ├── sessions.html
│       ├── users.html
└── static/
```


## 📌 Future Improvements

- 🌍 Multi-language support
- 📊 Analytics dashboard (usage stats)
- 📱 Responsive UI (mobile-friendly)
- 🧠 Swappable AI models (Gemini, OpenAI, etc.)

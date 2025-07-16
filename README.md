💬 AI Chat App (Gemini-powered)

An interactive, real-time AI chat application built with Spring Boot, WebSockets, and Thymeleaf, integrating **Gemini AI** for dynamic responses. This project supports multi-session chat, role-based access (user/admin), and includes advanced features like exporting chat history to PDF/JSON and **Kafka-based AI response publishing** for scalable asynchronous processing.

✨ Features

✅ User registration and login with Spring Security  
🧠 AI-powered chat via Gemini (Google AI)  
🔁 Real-time streaming response with WebSockets (SockJS + STOMP)  
📂 Chat session management (create, rename, delete)  
📜 Full chat history with timestamps  
📤 Export chat history as JSON or PDF  
📢 Kafka integration: AI responses are published to a Kafka topic (`gemini-response-topic`)  
🛠️ Admin Dashboard:  
  - View/delete all users and sessions  
  - View all chat entries  
🗑️ Individual chat message deletion  
🔐 Role-based authentication (USER / ADMIN)  

🔧 Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Backend      | Spring Boot, Spring MVC             |
| Security     | Spring Security, BCrypt             |
| Frontend     | HTML, Thymeleaf, JS                 |
| WebSocket    | SockJS + STOMP                      |
| Database     | MySQL / H2 (JPA, Hibernate)         |
| PDF Export   | iText / OpenPDF                     |
| JSON Export  | Jackson                             |
| AI Model     | Gemini API                          |
| Messaging    | Apache Kafka + Spring Kafka         |

🚀 Getting Started

1. Clone the repo

```bash
git clone https://github.com/Princee2245/AIChatApp.git
cd AIChatApp
```

2. Set up the database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aichat
spring.datasource.username=root
spring.datasource.password=yourpassword
```

3. Set up Kafka with Docker

Create `docker-compose.yml` in project root:

```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Then run:

```bash
docker-compose up -d
```

4. Run the application

```bash
./mvnw spring-boot:run
```

Open in browser:  
📍 `http://localhost:8080`

🧑‍💻 Default Roles

| Role  | Access                        |
|-------|-------------------------------|
| USER  | Chat, view own sessions       |
| ADMIN | View/delete all sessions, users |

You can manually assign roles via the `user.role` field in the database.

📂 Folder Structure

```
├── config/
├── controller/
├── entity/
├── model/
├── repository/
├── service/
├── templates/
│   ├── chat.html
│   ├── admin/
│       ├── dashboard.html
│       ├── sessions.html
│       ├── users.html
├── static/

```

📌 Future Improvements

🌍 Multi-language support  
📊 Analytics dashboard (usage stats)  
📱 Responsive UI (mobile-friendly)  
🧠 Swappable AI models (Gemini, OpenAI, etc.)  
📥 Kafka-based chat queue for async processing  

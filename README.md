# 🏥 Medical Appointment System API

A RESTful API built with **Spring Boot, JPA, and MySQL** to efficiently manage doctor-patient appointments. This API allows users to schedule, update, cancel, and retrieve appointments while ensuring secure role-based authentication for patients, doctors, and admins.

---

## ✨ Features

- 🏥 **Doctor-Patient Appointment Management** (Create, Read, Update, Delete)
- 🔍 **Search Appointments** by doctor, patient, or date
- 📆 **Check Available Slots** before booking an appointment
- 🔒 **Role-Based Authentication & Authorization** (Patients, Doctors, Admins)
- 🗄️ **MySQL Database for Data Persistence**
- 📜 **Swagger API Documentation**
- 🛠️ **Built with Maven, version-controlled using Git**
- 🚀 **Deployed on Apache Tomcat**

---

## 🏗️ Tech Stack

| Technology  | Description  |
|-------------|-------------|
| **Java 17+** | Programming Language  |
| **Spring Boot** | Backend Framework  |
| **Spring Security** | Authentication & Authorization  |
| **JPA & Hibernate** | ORM for Database Management  |
| **MySQL** | Relational Database  |
| **Maven** | Dependency Management  |
| **Swagger** | API Documentation  |
| **Apache Tomcat** | Deployment Server  |

---

## 🚀 Installation & Setup

### 🔧 Prerequisites
Ensure you have the following installed:
- **Java 17+**
- **Maven**
- **MySQL**
- **Postman** (Optional, for API testing)

### 🛠️ Steps to Run Locally

1️⃣ **Clone the Repository**
```bash
 git clone https://github.com/your-username/MedicalAppointmentSystemAPI.git
 cd MedicalAppointmentSystemAPI
```

2️⃣ **Configure Database** (MySQL)
- Create a database named `medical_appointment_system`
- Update `application.properties` with your MySQL credentials:
  
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medical_appointment_system
spring.datasource.username=root
spring.datasource.password=your_password
```

3️⃣ **Build & Run the Project**
```bash
mvn clean install
mvn spring-boot:run
```

4️⃣ **Access API Endpoints**
- API is available at `http://localhost:8080`
- Swagger Documentation: `http://localhost:8080/swagger-ui.html`

---

## 🔥 API Endpoints

| HTTP Method | Endpoint | Description |
|------------|---------|-------------|
| `POST` | `/appointments` | Book a new appointment |
| `GET` | `/appointments/{id}` | Get appointment details |
| `PUT` | `/appointments/{id}` | Update appointment details |
| `DELETE` | `/appointments/{id}` | Cancel an appointment |
| `GET` | `/appointments/doctor/{doctorId}` | Get appointments by doctor |
| `GET` | `/appointments/patient/{patientId}` | Get appointments by patient |
| `GET` | `/appointments/available-slots` | Check available slots |

---

## 🔒 Authentication & Roles
- 👨‍⚕️ **Doctor:** Can view, update, and manage appointments.
- 👤 **Patient:** Can book, view, and cancel their appointments.
- 👨‍💼 **Admin:** Can manage all appointments and users.

---

## 🛠 Contributing

1. **Fork the Repository**
2. **Create a Feature Branch** (`git checkout -b feature-name`)
3. **Commit Your Changes** (`git commit -m 'Add new feature'`)
4. **Push to Branch** (`git push origin feature-name`)
5. **Create a Pull Request**

---

## 📜 License
This project is **MIT Licensed** – feel free to modify and use it as needed.

---

## 📬 Contact
For any queries or collaboration, feel free to reach out:
📧 Email: vallamgurubabu@gmail.com 
🔗 LinkedIn: https://www.linkedin.com/public-profile/settings?trk=d_flagship3_profile_self_view_public_profile
---

### 🎯 Happy Coding! 🚀

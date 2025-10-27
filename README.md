# MediConnect - Healthcare Management System

![MediConnect Logo](https://img.shields.io/badge/MediConnect-Healthcare%20Management-blue?style=for-the-badge&logo=medical-cross)

A comprehensive healthcare management platform that seamlessly connects patients, doctors, and pharmacies through modern web technology and AI-powered assistance.

## 🌟 Overview

MediConnect is a full-stack healthcare management system designed to streamline medical processes, improve patient care, and enhance communication between healthcare stakeholders. The platform features role-based access control, intelligent appointment scheduling, digital prescription management, and an AI-powered chatbot assistant.

## 🚀 Key Features

### 👥 Multi-Role Support
- **Patients**: Book appointments, manage prescriptions, view medical history
- **Doctors**: Manage appointments, create prescriptions, track patient care
- **Pharmacies**: Process prescriptions, manage inventory, serve patients

### 🤖 AI-Powered Assistant
- 24/7 healthcare guidance and support
- Context-aware responses based on user role
- Natural language processing for better user interaction
- Integration with OpenRouter API using Gemma 2 model

### 📅 Smart Appointment Management
- Real-time availability checking
- Automated scheduling with conflict prevention
- Multiple appointment types (In-person, Video calls)
- Status tracking and notifications



### 🔒 Enterprise Security
- Session-based authentication
- Role-based access control (RBAC)
- Data encryption and secure storage
- CSRF protection and input validation

## 🛠️ Technology Stack

### Backend Technologies
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with session management
- **Database**: PostgreSQL with Spring Data JPA
- **Architecture**: RESTful API with MVC pattern
- **Build Tool**: Maven
- **Java Version**: 21

### Frontend Technologies
- **Core**: Vanilla JavaScript (ES6+)
- **Styling**: Modern CSS3 with custom properties
- **Design**: Responsive design with mobile-first approach
- **Icons**: Font Awesome 6.0.0
- **Architecture**: Component-based structure

### AI Integration
- **API**: OpenRouter API
- **Model**: Google Gemma 2 9B (Free tier)
- **Features**: Natural language processing, context awareness
- **Implementation**: RESTful integration with error handling

### Database & Storage
- **Primary DB**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Session Store**: JDBC-based session management
- **Connection Pooling**: HikariCP

## 📊 Database Structure

### Core Entities

#### Users Table
```sql
users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
```

#### Patients Table
```sql
patients (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    date_of_birth DATE,
    gender VARCHAR(20),
    phone VARCHAR(20),
    address TEXT,
    medical_history TEXT,
    allergies TEXT,
    current_medications TEXT,
    health_score INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
```

#### Doctors Table
```sql
doctors (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    license_number VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    experience INTEGER,
    education TEXT,
    certifications TEXT,
    hospital_affiliations TEXT,
    consultation_fee DECIMAL(10,2),
    daily_appointment_limit INTEGER DEFAULT 10,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
```

#### Appointments Table
```sql
appointments (
    id UUID PRIMARY KEY,
    patient_id UUID REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    appointment_date TIMESTAMP NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    reason TEXT,
    notes TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
```

#### Prescriptions Table
```sql
prescriptions (
    id UUID PRIMARY KEY,
    patient_id UUID REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    appointment_id UUID REFERENCES appointments(id),
    pharmacy_id UUID REFERENCES pharmacies(id),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
```

### Relationships
- **One-to-One**: User ↔ Patient/Doctor/Pharmacy
- **One-to-Many**: Doctor → Appointments, Patient → Appointments
- **Many-to-One**: Appointments → Prescriptions
- **Many-to-Many**: Prescriptions ↔ Medications (via junction table)

## 🏗️ Project Structure

```
mediconnect-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/mediconnect/
│   │   │   ├── config/                 # Configuration classes
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── SessionAuthenticationFilter.java
│   │   │   ├── controller/             # REST Controllers
│   │   │   │   ├── AppointmentController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ChatbotController.java
│   │   │   │   ├── DoctorController.java
│   │   │   │   ├── GeneralController.java
│   │   │   │   ├── PatientController.java
│   │   │   │   ├── PharmacyController.java
│   │   │   │   └── TestDataController.java
│   │   │   ├── dto/                    # Data Transfer Objects
│   │   │   │   ├── AppointmentBookingRequest.java
│   │   │   │   └── RegisterRequest.java
│   │   │   ├── entity/                 # JPA Entities
│   │   │   │   ├── Appointment.java
│   │   │   │   ├── Doctor.java
│   │   │   │   ├── Medication.java
│   │   │   │   ├── Patient.java
│   │   │   │   ├── Pharmacy.java
│   │   │   │   ├── Prescription.java
│   │   │   │   ├── PrescriptionMedication.java
│   │   │   │   └── User.java
│   │   │   ├── repository/             # Data Access Layer
│   │   │   │   ├── AppointmentRepository.java
│   │   │   │   ├── DoctorRepository.java
│   │   │   │   ├── MedicationRepository.java
│   │   │   │   ├── PatientRepository.java
│   │   │   │   ├── PharmacyRepository.java
│   │   │   │   ├── PrescriptionRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                # Business Logic Layer
│   │   │   │   ├── AppointmentService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ChatbotService.java
│   │   │   │   ├── DoctorService.java
│   │   │   │   ├── PatientService.java
│   │   │   │   ├── PharmacyService.java
│   │   │   │   ├── PrescriptionService.java
│   │   │   │   └── UserService.java
│   │   │   └── MediConnectApplication.java
│   │   └── resources/
│   │       ├── static/                 # Frontend Assets
│   │       │   ├── css/
│   │       │   │   ├── styles.css      # Main stylesheet
│   │       │   │   └── chatbot.css     # Chatbot styles
│   │       │   ├── js/
│   │       │   │   ├── app.js          # Main application logic
│   │       │   │   └── chatbot.js      # Chatbot functionality
│   │       │   └── index.html          # Single Page Application
│   │       ├── application.properties  # Configuration
│   │       └── schema.sql             # Database schema
├── target/                            # Build output
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🔌 API Endpoints

### Authentication APIs
```
POST /api/auth/login          # User login
POST /api/auth/register       # User registration
POST /api/auth/logout         # User logout
GET  /api/auth/user          # Get current user
```

### Patient APIs
```
GET  /api/patient/dashboard                    # Patient dashboard data
GET  /api/patient/appointments                 # Patient appointments
GET  /api/patient/prescriptions/my-prescriptions # Patient prescriptions
POST /api/appointments/book                    # Book appointment
```

### Doctor APIs
```
GET  /api/doctor/dashboard              # Doctor dashboard data
GET  /api/appointments/doctor/booked    # Doctor's appointments
GET  /api/appointments/doctor/today     # Today's appointments
PUT  /api/appointments/{id}/status      # Update appointment status
```

### Pharmacy APIs
```
GET  /api/pharmacy/dashboard            # Pharmacy dashboard data
GET  /api/pharmacy/prescriptions        # Pharmacy prescriptions
PUT  /api/prescriptions/{id}/status     # Update prescription status
```

### General APIs
```
GET  /api/appointments/doctors          # Available doctors list
GET  /api/doctors                      # All doctors
```

### Chatbot APIs
```
POST /api/chatbot/chat                 # Chat with AI assistant
POST /api/chatbot/clear                # Clear chat history
GET  /api/chatbot/test                 # Test chatbot service
```

### Test APIs (Development)
```
POST /api/test/create-test-doctors     # Create test data
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/mediconnect-springboot.git
   cd mediconnect-springboot
   ```

2. **Set up PostgreSQL Database**
   ```sql
   CREATE DATABASE mediconnect;
   CREATE USER mediconnect_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE mediconnect TO mediconnect_user;
   ```

3. **Configure Application Properties**
   ```properties
   # Update src/main/resources/application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/mediconnect
   spring.datasource.username=mediconnect_user
   spring.datasource.password=your_password
   
   # Add your OpenRouter API key
   openrouter.api.key=your_openrouter_api_key
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the Application**
   - Open browser to `http://localhost:8080`
   - Create test data: `POST http://localhost:8080/api/test/create-test-doctors`

### Test Accounts
After creating test data, you can use these accounts:
- **Doctor**: doctor1@test.com / password
- **Patient**: patient@test.com / password
- **Pharmacy**: pharmacy@test.com / password

## 🔧 Configuration

### Database Configuration
```properties
# PostgreSQL Configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/mediconnect}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:your_password}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Security Configuration
```properties
# Session Management
spring.session.store-type=jdbc
spring.session.timeout=30m
spring.session.jdbc.initialize-schema=always
```

### AI Configuration
```properties
# OpenRouter AI Configuration
openrouter.api.key=your_api_key_here
openrouter.api.url=https://openrouter.ai/api/v1/chat/completions
openrouter.model=google/gemma-2-9b-it:free
```

## 🎨 Design Concepts

### UI/UX Design Principles
- **Glass Morphism**: Modern translucent design elements
- **Gradient Aesthetics**: Sophisticated color transitions
- **Responsive Design**: Mobile-first approach
- **Accessibility**: WCAG compliant design
- **Premium Feel**: Enterprise-grade visual design

### Color Scheme
```css
--primary: #0066ff;           /* Primary blue */
--secondary: #00d4aa;         /* Teal accent */
--accent: #ff6b6b;           /* Coral highlight */
--neutral-900: #0a0e27;      /* Dark text */
--neutral-100: #f7fafc;      /* Light background */
```

### Typography
- **Primary Font**: Inter (Google Fonts)
- **Fallback**: System fonts (-apple-system, BlinkMacSystemFont)
- **Scale**: Modular scale with CSS custom properties

## 🧪 Testing

### Manual Testing
1. **User Registration/Login**: Test all user roles
2. **Appointment Booking**: End-to-end appointment flow
3. **Prescription Management**: Create and manage prescriptions
4. **Chatbot Interaction**: Test AI responses and formatting
5. **Responsive Design**: Test on various screen sizes

### API Testing
```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"patient@test.com","password":"password"}'

# Test chatbot
curl -X GET http://localhost:8080/api/chatbot/test
```

## 🚀 Deployment

### Production Considerations
1. **Environment Variables**: Use environment-specific configurations
2. **Database**: Set up production PostgreSQL instance
3. **Security**: Enable HTTPS and update CORS settings
4. **Monitoring**: Add application monitoring and logging
5. **Backup**: Implement database backup strategy

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/mediconnect-springboot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 👥 Development Team

### Lead Developer
**ALFRED MARIO SRIVIKASH**
- Full-stack developer and project architect
- Specializes in healthcare technology solutions
- Expert in Spring Boot, JavaScript, and AI integration

### Team Structure
- **Backend Development**: Spring Boot, PostgreSQL, API design
- **Frontend Development**: JavaScript, CSS, responsive design
- **AI Integration**: OpenRouter API, natural language processing
- **Database Design**: Entity modeling, relationship management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write meaningful commit messages
- Test thoroughly before submitting
- Update documentation as needed

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team
- Check the documentation and API endpoints

## 🔮 Future Enhancements

- [ ] Mobile application (React Native/Flutter)
- [ ] Advanced analytics and reporting
- [ ] Telemedicine video integration
- [ ] IoT device integration
- [ ] Machine learning for health predictions
- [ ] Multi-language support
- [ ] Advanced notification system
- [ ] Integration with external health systems

## 📊 Project Statistics

- **Lines of Code**: ~15,000+
- **API Endpoints**: 20+
- **Database Tables**: 8 core entities
- **Frontend Components**: 15+ interactive components
- **Security Features**: Role-based access, session management
- **AI Integration**: OpenRouter API with context awareness

---

**MediConnect** - Revolutionizing healthcare management through innovative technology.

*Built with ❤️ by the MediConnect development team*
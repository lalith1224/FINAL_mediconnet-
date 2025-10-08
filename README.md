# MediConnect - Healthcare Management System

A comprehensive healthcare management system built with Java Spring Boot backend and HTML/CSS/JavaScript frontend.

## Features

### For Patients
- User registration and authentication
- Dashboard with appointments and prescriptions overview
- Book and manage appointments
- View and request prescription refills
- Access medical records

### For Doctors
- Professional registration with license verification
- Dashboard with today's appointments and patient overview
- Manage patient appointments
- Create and manage prescriptions
- Set daily appointment limits

### For Pharmacies
- Pharmacy registration with license details
- Dashboard with pending prescriptions and inventory
- Manage medication inventory
- Process prescription requests
- Track low stock items

## Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database support
- **Maven** - Dependency management

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with Flexbox/Grid
- **Vanilla JavaScript** - No framework dependencies
- **Font Awesome** - Icons
- **Responsive Design** - Mobile-friendly interface

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd mediconnect-springboot
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application:
- Main application: http://localhost:8080
- H2 Database console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:mediconnect`
  - Username: `sa`
  - Password: `password`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `GET /api/auth/user` - Get current user

### Patient Endpoints
- `GET /api/patient/dashboard` - Patient dashboard data
- `GET /api/patient/appointments` - Patient appointments
- `POST /api/patient/appointments` - Book appointment
- `GET /api/patient/prescriptions` - Patient prescriptions
- `GET /api/patient/medical-records` - Medical records
- `POST /api/patient/prescriptions/{id}/refill` - Request refill

### Doctor Endpoints
- `GET /api/doctor/dashboard` - Doctor dashboard data
- `GET /api/doctor/patients` - Doctor's patients
- `POST /api/doctor/prescriptions` - Create prescription
- `PUT /api/doctor/appointment-limit` - Update appointment limit
- `GET /api/doctor/{doctorId}/availability` - Check availability

### Pharmacy Endpoints
- `GET /api/pharmacy/dashboard` - Pharmacy dashboard data
- `POST /api/pharmacy/inventory` - Add inventory item
- `PUT /api/pharmacy/prescriptions/{id}/status` - Update prescription status
- `POST /api/pharmacy/prescriptions` - Create prescription

### General Endpoints
- `GET /api/doctors` - List all doctors
- `PUT /api/appointments/{id}` - Update appointment
- `POST /api/ai/chat` - AI assistant chat

## Database Schema

The application uses the following main entities:

- **Users** - Base user authentication and profile
- **Patients** - Patient-specific information and medical history
- **Doctors** - Doctor profiles with specializations and credentials
- **Pharmacies** - Pharmacy information and operating details
- **Appointments** - Medical appointments between patients and doctors
- **Prescriptions** - Medical prescriptions with medications
- **Medications** - Individual medication details within prescriptions
- **InventoryItems** - Pharmacy medication inventory

## Configuration

### Database Configuration
The application is configured to use H2 in-memory database by default. To use PostgreSQL:

1. Update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mediconnect
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

2. Ensure PostgreSQL is running and the database exists.

### Security Configuration
- CSRF is disabled for API endpoints
- Session-based authentication
- BCrypt password encoding
- Role-based access control

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/mediconnect/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic services
│   │   └── MediConnectApplication.java
│   └── resources/
│       ├── static/         # Frontend assets
│       │   ├── css/
│       │   ├── js/
│       │   └── index.html
│       └── application.yml
└── test/                   # Test classes
```

### Adding New Features

1. **Backend**: Create entity → repository → service → controller
2. **Frontend**: Add HTML structure → CSS styling → JavaScript functionality
3. **Integration**: Connect frontend to backend APIs

## Testing

Run tests with:
```bash
mvn test
```

## Deployment

### Production Build
```bash
mvn clean package -Pproduction
```

### Docker Deployment
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/mediconnect-springboot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

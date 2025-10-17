// MediConnect Frontend Application
class MediConnectApp {
    constructor() {
        this.currentUser = null;
        this.currentPage = 'landing';
        this.init();
    }

    init() {
        this.checkAuthStatus();
        this.setupEventListeners();
    }

    // Authentication Methods
    async checkAuthStatus() {
        try {
            const response = await fetch('/api/auth/user', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const user = await response.json();
                this.currentUser = user;
                this.updateNavigation(true);
                this.showDashboard(user.role);
            } else {
                this.updateNavigation(false);
                this.showPage('landing');
            }
        } catch (error) {
            console.error('Auth check failed:', error);
            this.updateNavigation(false);
            this.showPage('landing');
        }
    }

    async login(email, password) {
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                this.currentUser = data.user;
                this.updateNavigation(true);
                this.showDashboard(data.user.role);
                this.showToast('Login successful!', 'success');
                return true;
            } else {
                this.showToast(data.message || 'Login failed', 'error');
                return false;
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showToast('Login failed. Please try again.', 'error');
            return false;
        }
    }

    async register(formData) {
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(formData)
            });

            const data = await response.json();

            if (response.ok) {
                this.currentUser = data.user;
                this.updateNavigation(true);
                this.showDashboard(data.user.role);
                this.showToast('Registration successful!', 'success');
                return true;
            } else {
                this.showToast(data.message || 'Registration failed', 'error');
                return false;
            }
        } catch (error) {
            console.error('Registration error:', error);
            this.showToast('Registration failed. Please try again.', 'error');
            return false;
        }
    }

    async logout() {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
            
            this.currentUser = null;
            this.updateNavigation(false);
            this.showPage('landing');
            this.showToast('Logged out successfully', 'success');
        } catch (error) {
            console.error('Logout error:', error);
            this.showToast('Logout failed', 'error');
        }
    }

    // Dashboard Methods - ENHANCED DATA LOADING
    async loadPatientDashboard() {
        try {
            console.log('Loading patient dashboard...');
            
            // Load comprehensive dashboard data
            const response = await fetch('/api/patient/dashboard', {
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                console.log('Patient dashboard data:', data);
                this.populatePatientDashboard(data);
                
                // Load additional data for different sections
                await this.loadPatientAdditionalData();
            } else {
                console.error('Failed to load patient dashboard:', response.status);
                this.showToast('Failed to load dashboard data', 'error');
                // Show empty state
                this.populatePatientDashboard({});
            }
        } catch (error) {
            console.error('Patient dashboard load error:', error);
            this.showToast('Failed to load dashboard. Please try again.', 'error');
            // Show empty state
            this.populatePatientDashboard({});
        }
    }

    async loadPatientAdditionalData() {
        try {
            // Load appointments
            const appointmentsResp = await fetch('/api/appointments/my-appointments', {
                credentials: 'include'
            });
            
            if (appointmentsResp.ok) {
                const appointments = await appointmentsResp.json();
                this.renderPatientAppointments(appointments);
                this.renderPatientUpcomingAppointments(appointments);
            }

            // Load prescriptions
            const prescriptionsResp = await fetch('/api/patient/prescriptions/my-prescriptions', {
                credentials: 'include'
            });
            
            if (prescriptionsResp.ok) {
                const prescriptions = await prescriptionsResp.json();
                this.renderPatientPrescriptions(prescriptions);
            } else {
                const error = await prescriptionsResp.json().catch(() => ({}));
                console.error('Failed to load prescriptions:', error);
                this.showToast('Failed to load prescriptions', 'error');
            }
        } catch (error) {
            console.error('Error loading patient additional data:', error);
        }
    }

    async loadDoctorDashboard() {
        try {
            console.log('Loading doctor dashboard...');
            
            // Load comprehensive dashboard data
            const response = await fetch('/api/doctor/dashboard', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const data = await response.json();
                console.log('Doctor dashboard data:', data);
                this.populateDoctorDashboard(data);
                
                // Load additional data
                await this.loadDoctorAdditionalData();
            } else {
                console.error('Failed to load doctor dashboard:', response.status);
                this.showToast('Failed to load dashboard data', 'error');
                // Show empty state
                this.populateDoctorDashboard({});
            }
        } catch (error) {
            console.error('Doctor dashboard load error:', error);
            this.showToast('Failed to load dashboard. Please try again.', 'error');
            // Show empty state
            this.populateDoctorDashboard({});
        }
    }

    async loadDoctorAdditionalData() {
        try {
            // Load all appointments for doctor
            const appointmentsResp = await fetch('/api/appointments/doctor/booked', {
                credentials: 'include'
            });
            
            if (appointmentsResp.ok) {
                const appointments = await appointmentsResp.json();
                this.renderDoctorAllAppointments(appointments);
            }

            // Load today's appointments specifically
            const todayResp = await fetch('/api/appointments/doctor/today', {
                credentials: 'include'
            });
            
            if (todayResp.ok) {
                const todayAppointments = await todayResp.json();
                this.renderDoctorTodayAppointments(todayAppointments);
            }
        } catch (error) {
            console.error('Error loading doctor additional data:', error);
        }
    }

    async loadPharmacyDashboard() {
        try {
            const response = await fetch('/api/pharmacy/dashboard', {
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                this.populatePharmacyDashboard(data);
            } else {
                this.showToast('Failed to load dashboard', 'error');
                // Show empty state
                this.populatePharmacyDashboard({});
            }
        } catch (error) {
            console.error('Dashboard load error:', error);
            this.showToast('Failed to load dashboard. Please try again.', 'error');
            // Show empty state
            this.populatePharmacyDashboard({});
        }
    }

    // Enhanced UI Population Methods
    populatePatientDashboard(data) {
        if (!this.currentUser) return;

        // Update user info
        document.getElementById('patient-name').textContent = this.currentUser.firstName;

        // Update statistics - with fallback values
        const stats = data.stats || {};
        document.getElementById('patient-appointments-count').textContent = stats.totalAppointments || 0;
        document.getElementById('patient-upcoming-count').textContent = stats.upcomingAppointments || 0;
        document.getElementById('patient-completed-count').textContent = stats.completedAppointments || 0;
        document.getElementById('patient-prescriptions-count').textContent = stats.activePrescriptions || 0;

        // Populate upcoming appointments from dashboard data if available
        const upcomingContainer = document.getElementById('patient-upcoming-appointments');
        if (data.upcomingAppointments && data.upcomingAppointments.length > 0) {
            upcomingContainer.innerHTML = data.upcomingAppointments.map(appointment =>
                this.createPatientAppointmentCard(appointment, false)
            ).join('');
        } else {
            upcomingContainer.innerHTML = this.createEmptyState('No upcoming appointments', 'fas fa-calendar-times');
        }

        // Populate prescriptions from dashboard data if available
        const prescriptionsContainer = document.getElementById('patient-prescriptions');
        if (data.activePrescriptions && data.activePrescriptions.length > 0) {
            prescriptionsContainer.innerHTML = data.activePrescriptions.map(prescription =>
                this.createPrescriptionCard(prescription)
            ).join('');
        } else {
            prescriptionsContainer.innerHTML = this.createEmptyState('No active prescriptions', 'fas fa-prescription-bottle');
        }
    }

    populateDoctorDashboard(data) {
        if (!this.currentUser) return;

        // Update user info
        document.getElementById('doctor-name').textContent = this.currentUser.lastName;

        // Update statistics - with fallback values and proper data structure handling
        const stats = data.stats || {};
        document.getElementById('doctor-today-appointments').textContent = stats.todayAppointments || stats.todayPatients || 0;
        document.getElementById('doctor-patients-count').textContent = stats.totalPatients || stats.patientCount || 0;
        document.getElementById('doctor-prescriptions-count').textContent = stats.totalPrescriptions || 0;
        document.getElementById('doctor-pending-reviews').textContent = stats.pendingReviews || 0;
        document.getElementById('doctor-ai-insights').textContent = stats.aiInsights || 0;

        // Populate today's appointments from dashboard data if available
        const todayAppointmentsContainer = document.getElementById('doctor-appointments');
        if (data.todayAppointments && data.todayAppointments.length > 0) {
            todayAppointmentsContainer.innerHTML = data.todayAppointments.map(appointment =>
                this.createDoctorAppointmentCard(appointment)
            ).join('');
        } else {
            todayAppointmentsContainer.innerHTML = this.createEmptyState('No appointments today', 'fas fa-calendar-check');
        }

        // Populate upcoming appointments if available
        const upcomingContainer = document.getElementById('doctor-upcoming-appointments');
        if (upcomingContainer && data.upcomingAppointments) {
            if (data.upcomingAppointments.length > 0) {
                upcomingContainer.innerHTML = data.upcomingAppointments.map(appointment =>
                    this.createDoctorAppointmentCard(appointment)
                ).join('');
            } else {
                upcomingContainer.innerHTML = this.createEmptyState('No upcoming appointments', 'fas fa-calendar-alt');
            }
        }
    }

    populatePharmacyDashboard(data) {
        if (!this.currentUser) return;

        const pharmacy = data.pharmacy || {};
        document.getElementById('pharmacy-name').textContent = pharmacy.pharmacyName || this.currentUser.pharmacyName || 'Pharmacy';

        // Update statistics
        document.getElementById('pharmacy-pending-prescriptions').textContent = data.pendingPrescriptionsCount || data.pendingPrescriptions?.length || 0;
        document.getElementById('pharmacy-inventory-count').textContent = data.totalInventoryItems || 0;

        // Populate pending prescriptions
        const prescriptionsContainer = document.getElementById('pharmacy-prescriptions');
        const pendingPrescriptions = data.pendingPrescriptions || [];
        if (pendingPrescriptions.length > 0) {
            prescriptionsContainer.innerHTML = pendingPrescriptions.map(prescription => 
                this.createPrescriptionCard(prescription, true)
            ).join('');
        } else {
            prescriptionsContainer.innerHTML = this.createEmptyState('No pending prescriptions', 'fas fa-prescription');
        }

        // Populate low stock items
        const lowStockContainer = document.getElementById('pharmacy-low-stock');
        const lowStockItems = data.lowStockItems || [];
        if (lowStockItems.length > 0) {
            lowStockContainer.innerHTML = lowStockItems.map(item => 
                this.createInventoryCard(item)
            ).join('');
        } else {
            lowStockContainer.innerHTML = this.createEmptyState('No low stock items', 'fas fa-boxes');
        }
    }

    // Enhanced Card Creation Methods
    createPatientAppointmentCard(appointment, showDetails = false) {
        const date = new Date(appointment.appointmentDate);
        const doctorName = appointment.doctor ? 
            `Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}` :
            appointment.doctorName ? 
                `Dr. ${appointment.doctorName}` : 
                'Unknown Doctor';

        const specialization = appointment.doctor?.specialization || appointment.doctorSpecialization || 'General';
        const status = appointment.status || 'SCHEDULED';

        if (showDetails) {
            return `
                <div class="appointment-card">
                    <div class="appointment-info">
                        <h4>${doctorName}</h4>
                        <p><strong>Specialization:</strong> ${specialization}</p>
                        <p><strong>Date & Time:</strong> ${date.toLocaleString()}</p>
                        <p><strong>Type:</strong> ${appointment.appointmentType || 'IN_PERSON'}</p>
                        <p><strong>Reason:</strong> ${appointment.reason || 'Not specified'}</p>
                        <p><strong>Status:</strong> <span class="status ${status.toLowerCase()}">${this.getStatusDisplay(status)}</span></p>
                        ${appointment.notes ? `<p><strong>Notes:</strong> ${appointment.notes}</p>` : ''}
                        ${appointment.diagnosis ? `<p><strong>Diagnosis:</strong> ${appointment.diagnosis}</p>` : ''}
                        ${appointment.treatmentPlan ? `<p><strong>Treatment Plan:</strong> ${appointment.treatmentPlan}</p>` : ''}
                    </div>
                    <div class="appointment-actions">
                        ${status === 'SCHEDULED' ?
                            `<button onclick="app.cancelAppointment('${appointment.id}')" class="btn btn-danger btn-sm">Cancel</button>` :
                            ''}
                    </div>
                </div>
            `;
        }

        return `
            <div class="appointment-item">
                <h4>${doctorName}</h4>
                <p><i class="fas fa-calendar"></i> ${date.toLocaleDateString()}</p>
                <p><i class="fas fa-clock"></i> ${date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</p>
                <p><i class="fas fa-stethoscope"></i> ${specialization}</p>
                <p><i class="fas fa-info-circle"></i> ${appointment.reason || 'Consultation'}</p>
                <span class="status-badge status-${status.toLowerCase()}">${this.getStatusDisplay(status)}</span>
            </div>
        `;
    }

    createDoctorAppointmentCard(appointment) {
        const date = new Date(appointment.appointmentDate);
        const patientName = appointment.patient ? 
            `${appointment.patient.firstName} ${appointment.patient.lastName}` :
            appointment.patientName || 'Unknown Patient';

        const patientEmail = appointment.patient?.email || appointment.patientEmail || 'Not available';
        const patientPhone = appointment.patient?.phone || appointment.patientPhone || 'Not available';
        const status = appointment.status || 'SCHEDULED';

        return `
            <div class="appointment-card">
                <div class="appointment-info">
                    <h4>Patient: ${patientName}</h4>
                    <div class="patient-details">
                        <p><strong>Email:</strong> ${patientEmail}</p>
                        <p><strong>Phone:</strong> ${patientPhone}</p>
                    </div>
                    <p><strong>Date & Time:</strong> ${date.toLocaleString()}</p>
                    <p><strong>Type:</strong> ${appointment.appointmentType || 'IN_PERSON'}</p>
                    <p><strong>Reason:</strong> ${appointment.reason || 'Not specified'}</p>
                    <p><strong>Status:</strong> <span class="status ${status.toLowerCase()}">${this.getStatusDisplay(status)}</span></p>
                </div>
                <div class="appointment-actions">
                    ${status === 'SCHEDULED' ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'CONFIRMED')" class="btn btn-success btn-sm">Confirm</button>` : 
                        ''}
                    ${status === 'CONFIRMED' ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'COMPLETED')" class="btn btn-primary btn-sm">Complete</button>` : 
                        ''}
                    ${(status === 'SCHEDULED' || status === 'CONFIRMED') ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'CANCELLED')" class="btn btn-danger btn-sm">Cancel</button>` : 
                        ''}
                </div>
            </div>
        `;
    }

    createPrescriptionCard(prescription, isPharmacy = false) {
        const date = new Date(prescription.createdAt || prescription.date || Date.now());
        const doctorName = prescription.doctor ? 
            `Dr. ${prescription.doctor.firstName} ${prescription.doctor.lastName}` :
            prescription.doctorName ? 
                `Dr. ${prescription.doctorName}` : 
                'Unknown Doctor';
        
        const patientName = isPharmacy && prescription.patient ? 
            `${prescription.patient.firstName} ${prescription.patient.lastName}` :
            isPharmacy && prescription.patientName ? 
                prescription.patientName : '';
        
        const medicationCount = prescription.medications ? 
            prescription.medications.length : 
            prescription.medicationCount || 0;
        
        const status = prescription.status || 'ACTIVE';

        return `
            <div class="prescription-item">
                <h4>Prescription #${prescription.id ? prescription.id.substring(0, 8) : 'N/A'}</h4>
                ${isPharmacy && patientName ? `<p><i class="fas fa-user"></i> ${patientName}</p>` : ''}
                <p><i class="fas fa-user-md"></i> ${doctorName}</p>
                <p><i class="fas fa-calendar"></i> ${date.toLocaleDateString()}</p>
                <p><i class="fas fa-pills"></i> ${medicationCount} medications</p>
                <span class="status-badge status-${status.toLowerCase()}">${status}</span>
            </div>
        `;
    }

    createInventoryCard(item) {
        return `
            <div class="inventory-item">
                <h4>${item.medicineName || item.name || 'Unknown Medicine'}</h4>
                <p><i class="fas fa-boxes"></i> Stock: ${item.currentStock || item.quantity || 0}</p>
                <p><i class="fas fa-exclamation-triangle"></i> Min Level: ${item.minStockLevel || item.minQuantity || 0}</p>
                <p><i class="fas fa-dollar-sign"></i> Price: $${item.price || 0}</p>
                ${item.expiryDate ? `<p><i class="fas fa-calendar-times"></i> Expires: ${new Date(item.expiryDate).toLocaleDateString()}</p>` : ''}
            </div>
        `;
    }

    createEmptyState(message, icon) {
        return `
            <div class="empty-state">
                <i class="${icon}"></i>
                <h3>No Data Available</h3>
                <p>${message}</p>
            </div>
        `;
    }

    // Enhanced Rendering Methods for Additional Data
    renderPatientAppointments(appointments) {
        const container = document.getElementById('patient-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = this.createEmptyState('No appointments found', 'fas fa-calendar-times');
            return;
        }

        container.innerHTML = appointments.map(appointment => 
            this.createPatientAppointmentCard(appointment, true)
        ).join('');
    }

    renderPatientUpcomingAppointments(appointments) {
        const container = document.getElementById('patient-upcoming-appointments');
        if (!container) return;

        // Filter upcoming appointments (scheduled or confirmed, and in the future)
        const now = new Date();
        const upcoming = appointments.filter(apt => {
            const aptDate = new Date(apt.appointmentDate);
            const isFuture = aptDate > now;
            const isActive = apt.status === 'SCHEDULED' || apt.status === 'CONFIRMED';
            return isFuture && isActive;
        });

        if (upcoming.length === 0) {
            container.innerHTML = this.createEmptyState('No upcoming appointments', 'fas fa-calendar-times');
            return;
        }

        container.innerHTML = upcoming.map(appointment => 
            this.createPatientAppointmentCard(appointment, false)
        ).join('');
    }

    renderPatientPrescriptions(prescriptions) {
        const container = document.getElementById('patient-prescriptions');
        if (!container) return;

        // Filter active prescriptions
        const activePrescriptions = prescriptions.filter(presc => 
            presc.status === 'ACTIVE' || presc.status === 'PENDING'
        );

        if (activePrescriptions.length === 0) {
            container.innerHTML = this.createEmptyState('No active prescriptions', 'fas fa-prescription-bottle');
            return;
        }

        container.innerHTML = activePrescriptions.map(prescription => 
            this.createPrescriptionCard(prescription)
        ).join('');
    }

    renderDoctorTodayAppointments(appointments) {
        const container = document.getElementById('doctor-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = this.createEmptyState('No appointments today', 'fas fa-calendar-check');
            return;
        }

        container.innerHTML = appointments.map(appointment => 
            this.createDoctorAppointmentCard(appointment)
        ).join('');
    }

    renderDoctorAllAppointments(appointments) {
        const container = document.getElementById('doctor-all-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = this.createEmptyState('No appointments found', 'fas fa-calendar-times');
            return;
        }

        container.innerHTML = appointments.map(appointment => 
            this.createDoctorAppointmentCard(appointment)
        ).join('');
    }

    // UI Methods
    showPage(pageId) {
        // Hide all pages
        document.querySelectorAll('.page').forEach(page => {
            page.style.display = 'none';
        });

        // Show selected page
        const page = document.getElementById(pageId + '-page') || document.getElementById(pageId);
        if (page) {
            page.style.display = 'block';
            this.currentPage = pageId;
        }
    }

    showDashboard(role) {
        const dashboardId = role.toLowerCase() + '-dashboard';
        this.showPage(dashboardId);
        
        // Load dashboard data
        switch (role) {
            case 'PATIENT':
                this.loadPatientDashboard();
                break;
            case 'DOCTOR':
                this.loadDoctorDashboard();
                break;
            case 'PHARMACY':
                this.loadPharmacyDashboard();
                break;
        }
    }

    updateNavigation(isLoggedIn) {
        const authSection = document.getElementById('auth-section');
        const userSection = document.getElementById('user-section');
        const userName = document.getElementById('user-name');

        if (isLoggedIn && this.currentUser) {
            authSection.style.display = 'none';
            userSection.style.display = 'flex';
            userName.textContent = this.currentUser.firstName + ' ' + this.currentUser.lastName;
        } else {
            authSection.style.display = 'flex';
            userSection.style.display = 'none';
        }
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;

        const container = document.getElementById('toast-container');
        container.appendChild(toast);

        // Remove toast after 5 seconds
        setTimeout(() => {
            toast.remove();
        }, 5000);
    }

    getStatusDisplay(status) {
        const statusMap = {
            'SCHEDULED': 'Scheduled',
            'CONFIRMED': 'Confirmed',
            'COMPLETED': 'Completed',
            'CANCELLED': 'Cancelled',
            'ACTIVE': 'Active',
            'PENDING': 'Pending',
            'FULFILLED': 'Fulfilled'
        };
        return statusMap[status] || status;
    }

    setupEventListeners() {
        // Login form
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(loginForm);
                const email = formData.get('email');
                const password = formData.get('password');
                
                await this.login(email, password);
            });
        }

        // Register form
        const registerForm = document.getElementById('register-form');
        if (registerForm) {
            registerForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(registerForm);
                
                const registerData = {
                    email: formData.get('email'),
                    firstName: formData.get('firstName'),
                    lastName: formData.get('lastName'),
                    password: formData.get('password'),
                    role: formData.get('role'),
                    dateOfBirth: formData.get('dateOfBirth') || null,
                    gender: formData.get('gender') || null,
                    phone: formData.get('phone') || null,
                    licenseNumber: formData.get('licenseNumber') || null,
                    specialization: formData.get('specialization') || null,
                    experience: formData.get('experience') ? parseInt(formData.get('experience')) : null,
                    pharmacyName: formData.get('pharmacyName') || null,
                    address: formData.get('address') || null
                };
                
                await this.register(registerData);
            });
        }

        // Book appointment form
        const bookAppointmentForm = document.getElementById('book-appointment-form');
        if (bookAppointmentForm) {
            bookAppointmentForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(bookAppointmentForm);
                
                const appointmentDate = formData.get('appointmentDate');
                const appointmentTime = formData.get('appointmentTime');
                const appointmentDateTime = new Date(`${appointmentDate}T${appointmentTime}`);
                
                const appointmentData = {
                    doctorId: formData.get('doctorId'),
                    appointmentDate: appointmentDateTime.toISOString(),
                    appointmentType: formData.get('appointmentType'),
                    reason: formData.get('reason')
                };
                
                await this.bookAppointment(appointmentData);
            });
        }
    }

    // Appointment Methods
    async updateAppointmentStatus(appointmentId, status) {
        try {
            const response = await fetch(`/api/appointments/${appointmentId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ status: status })
            });

            const data = await response.json();

            if (response.ok) {
                const statusMessage = status === 'CONFIRMED' ? 'confirmed' : status.toLowerCase();
                this.showToast(`Appointment ${statusMessage} successfully!`, 'success');
                // Reload the current dashboard to reflect changes
                if (this.currentUser.role === 'DOCTOR') {
                    this.loadDoctorDashboard();
                } else if (this.currentUser.role === 'PATIENT') {
                    this.loadPatientDashboard();
                }
            } else {
                this.showToast(data.message || 'Failed to update appointment status', 'error');
            }
        } catch (error) {
            console.error('Status update error:', error);
            this.showToast('Failed to update appointment status. Please try again.', 'error');
        }
    }

    async cancelAppointment(appointmentId) {
        if (!confirm('Are you sure you want to cancel this appointment?')) return;
        try {
            const response = await fetch(`/api/appointments/${appointmentId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            const data = await response.json().catch(() => ({}));
            if (response.ok) {
                this.showToast('Appointment cancelled successfully!', 'success');
                // Reload current dashboard to reflect changes
                if (this.currentUser?.role === 'DOCTOR') {
                    await this.loadDoctorDashboard();
                } else if (this.currentUser?.role === 'PATIENT') {
                    await this.loadPatientDashboard();
                }
            } else {
                this.showToast(data.message || 'Failed to cancel appointment', 'error');
            }
        } catch (error) {
            console.error('Cancel appointment error:', error);
            this.showToast('Failed to cancel appointment. Please try again.', 'error');
        }
    }

    async bookAppointment(appointmentData) {
        try {
            const response = await fetch('/api/appointments/book', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(appointmentData)
            });

            const data = await response.json();

            if (response.ok) {
                this.showToast('Appointment booked successfully!', 'success');
                this.showDashboard(this.currentUser.role);
                return true;
            } else {
                this.showToast(data.message || 'Failed to book appointment', 'error');
                return false;
            }
        } catch (error) {
            console.error('Appointment booking error:', error);
            this.showToast('Failed to book appointment. Please try again.', 'error');
            return false;
        }
    }

    async loadDoctors() {
        try {
            const response = await fetch('/api/appointments/doctors', {
                credentials: 'include'
            });

            if (response.ok) {
                const doctors = await response.json();
                const doctorSelect = document.getElementById('appointment-doctor');
                
                doctorSelect.innerHTML = '<option value="">Choose a doctor...</option>';
                doctors.forEach(doctor => {
                    const option = document.createElement('option');
                    option.value = doctor.id;
                    option.textContent = `${doctor.firstName} ${doctor.lastName} - ${doctor.specialization || 'General'}`;
                    doctorSelect.appendChild(option);
                });
            } else {
                this.showToast('Failed to load doctors', 'error');
            }
        } catch (error) {
            console.error('Error loading doctors:', error);
            this.showToast('Failed to load doctors', 'error');
        }
    }
}

// Global functions for HTML onclick handlers
function showLogin() {
    app.showPage('login');
}

function showRegister() {
    app.showPage('register');
}

function logout() {
    app.logout();
}

function showBookAppointment() {
    app.showPage('book-appointment');
    app.loadDoctors();
    
    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('appointment-date').min = today;
}

function showPatientDashboard() {
    app.showDashboard('PATIENT');
}

function toggleRoleFields() {
    const role = document.getElementById('register-role').value;
    const roleFields = document.querySelectorAll('.role-fields');
    
    // Hide all role fields
    roleFields.forEach(field => {
        field.style.display = 'none';
    });
    
    // Show relevant role fields
    if (role) {
        const targetField = document.getElementById(role.toLowerCase() + '-fields');
        if (targetField) {
            targetField.style.display = 'block';
        }
    }
}

// Initialize app when DOM is loaded
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new MediConnectApp();
});
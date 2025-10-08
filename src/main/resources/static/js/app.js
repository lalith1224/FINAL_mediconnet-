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

    // Dashboard Methods
    async loadPatientDashboard() {
        try {
            // Load dashboard stats
            const statsResponse = await fetch('/api/appointments/dashboard-stats', {
                credentials: 'include'
            });
            
            if (statsResponse.ok) {
                const stats = await statsResponse.json();
                this.populatePatientStats(stats);
            }

            // Load all patient appointments
            const appointmentsResponse = await fetch('/api/appointments/my-appointments', {
                credentials: 'include'
            });
            
            if (appointmentsResponse.ok) {
                const appointments = await appointmentsResponse.json();
                this.renderPatientAppointments(appointments);
            }

            // Load upcoming appointments separately
            const upcomingResponse = await fetch('/api/appointments/upcoming', {
                credentials: 'include'
            });
            
            if (upcomingResponse.ok) {
                const upcomingAppointments = await upcomingResponse.json();
                this.renderPatientUpcomingAppointments(upcomingAppointments);
            }
        } catch (error) {
            console.error('Dashboard load error:', error);
            this.showToast('Failed to load dashboard', 'error');
        }
    }

    async loadDoctorDashboard() {
        try {
            // Load dashboard stats
            const statsResponse = await fetch('/api/appointments/dashboard-stats', {
                credentials: 'include'
            });
            
            if (statsResponse.ok) {
                const stats = await statsResponse.json();
                this.populateDoctorStats(stats);
            }

            // Load doctor's upcoming appointments
            const upcomingResponse = await fetch('/api/appointments/upcoming', {
                credentials: 'include'
            });
            
            if (upcomingResponse.ok) {
                const upcomingAppointments = await upcomingResponse.json();
                this.renderDoctorAppointments(upcomingAppointments);
            }

            // Load all doctor's appointments
            const allResponse = await fetch('/api/appointments/my-appointments', {
                credentials: 'include'
            });
            
            if (allResponse.ok) {
                const allAppointments = await allResponse.json();
                this.renderDoctorAllAppointments(allAppointments);
            }
        } catch (error) {
            console.error('Dashboard load error:', error);
            this.showToast('Failed to load dashboard', 'error');
        }
    }

    populateDoctorStats(stats) {
        document.getElementById('doctor-today-appointments').textContent = stats.todayAppointments || 0;
        document.getElementById('doctor-patients-count').textContent = stats.patientCount || 0;
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
            }
        } catch (error) {
            console.error('Dashboard load error:', error);
            this.showToast('Failed to load dashboard', 'error');
        }
    }

    populatePatientStats(stats) {
        document.getElementById('patient-appointments-count').textContent = stats.totalAppointments || 0;
        document.getElementById('patient-upcoming-count').textContent = stats.upcomingAppointments || 0;
        document.getElementById('patient-completed-count').textContent = stats.completedAppointments || 0;
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

    populatePatientDashboard(data) {
        document.getElementById('patient-name').textContent = this.currentUser.firstName;
        document.getElementById('patient-appointments-count').textContent = data.upcomingAppointments.length;
        document.getElementById('patient-prescriptions-count').textContent = data.activePrescriptions.length;

        // Populate appointments
        const appointmentsList = document.getElementById('patient-appointments');
        if (data.upcomingAppointments.length === 0) {
            appointmentsList.innerHTML = this.createEmptyState('No upcoming appointments', 'fas fa-calendar-times');
        } else {
            appointmentsList.innerHTML = data.upcomingAppointments.map(appointment => 
                this.createAppointmentCard(appointment)
            ).join('');
        }

        // Populate prescriptions
        const prescriptionsList = document.getElementById('patient-prescriptions');
        if (data.activePrescriptions.length === 0) {
            prescriptionsList.innerHTML = this.createEmptyState('No active prescriptions', 'fas fa-prescription-bottle');
        } else {
            prescriptionsList.innerHTML = data.activePrescriptions.map(prescription => 
                this.createPrescriptionCard(prescription)
            ).join('');
        }
    }

    populateDoctorDashboard(data) {
        document.getElementById('doctor-name').textContent = this.currentUser.firstName + ' ' + this.currentUser.lastName;
        document.getElementById('doctor-today-appointments').textContent = data.todayAppointments.length;
        document.getElementById('doctor-patients-count').textContent = data.totalPatients;

        // Populate today's appointments
        const appointmentsList = document.getElementById('doctor-appointments');
        if (data.todayAppointments.length === 0) {
            appointmentsList.innerHTML = this.createEmptyState('No appointments today', 'fas fa-calendar-check');
        } else {
            appointmentsList.innerHTML = data.todayAppointments.map(appointment => 
                this.createAppointmentCard(appointment, true)
            ).join('');
        }
    }

    populatePharmacyDashboard(data) {
        document.getElementById('pharmacy-name').textContent = data.pharmacy.pharmacyName;
        document.getElementById('pharmacy-pending-prescriptions').textContent = data.pendingPrescriptions.length;
        document.getElementById('pharmacy-inventory-count').textContent = data.totalInventoryItems;

        // Populate pending prescriptions
        const prescriptionsList = document.getElementById('pharmacy-prescriptions');
        if (data.pendingPrescriptions.length === 0) {
            prescriptionsList.innerHTML = this.createEmptyState('No pending prescriptions', 'fas fa-prescription');
        } else {
            prescriptionsList.innerHTML = data.pendingPrescriptions.map(prescription => 
                this.createPrescriptionCard(prescription, true)
            ).join('');
        }

        // Populate low stock items
        const lowStockList = document.getElementById('pharmacy-low-stock');
        if (data.lowStockItems.length === 0) {
            lowStockList.innerHTML = this.createEmptyState('No low stock items', 'fas fa-boxes');
        } else {
            lowStockList.innerHTML = data.lowStockItems.map(item => 
                this.createInventoryCard(item)
            ).join('');
        }
    }

    createAppointmentCard(appointment, isDoctor = false) {
        const date = new Date(appointment.appointmentDate);
        const displayName = isDoctor ? appointment.patientName : appointment.doctorName;
        
        return `
            <div class="appointment-item">
                <h4>${displayName}</h4>
                <p><i class="fas fa-calendar"></i> ${date.toLocaleDateString()}</p>
                <p><i class="fas fa-clock"></i> ${date.toLocaleTimeString()}</p>
                <p><i class="fas fa-stethoscope"></i> ${appointment.doctorSpecialization || 'General'}</p>
                <p><i class="fas fa-info-circle"></i> ${appointment.reason || 'No reason specified'}</p>
                <span class="status-badge status-${appointment.status.toLowerCase()}">${appointment.status}</span>
            </div>
        `;
    }

    createPrescriptionCard(prescription, isPharmacy = false) {
        const date = new Date(prescription.createdAt);
        const doctorName = `Dr. ${prescription.doctor.user.firstName} ${prescription.doctor.user.lastName}`;
        const patientName = isPharmacy ? 
            `${prescription.patient.user.firstName} ${prescription.patient.user.lastName}` : '';
        
        return `
            <div class="prescription-item">
                <h4>Prescription #${prescription.id.substring(0, 8)}</h4>
                ${isPharmacy ? `<p><i class="fas fa-user"></i> ${patientName}</p>` : ''}
                <p><i class="fas fa-user-md"></i> ${doctorName}</p>
                <p><i class="fas fa-calendar"></i> ${date.toLocaleDateString()}</p>
                <p><i class="fas fa-pills"></i> ${prescription.medications?.length || 0} medications</p>
                <span class="status-badge status-${prescription.status.toLowerCase()}">${prescription.status}</span>
            </div>
        `;
    }

    createInventoryCard(item) {
        return `
            <div class="inventory-item">
                <h4>${item.medicineName}</h4>
                <p><i class="fas fa-boxes"></i> Stock: ${item.currentStock}</p>
                <p><i class="fas fa-exclamation-triangle"></i> Min Level: ${item.minStockLevel}</p>
                <p><i class="fas fa-dollar-sign"></i> Price: $${item.price}</p>
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

    renderDoctorAllAppointments(appointments) {
        const container = document.getElementById('doctor-all-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = '<p class="no-appointments">No appointments found.</p>';
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="appointment-card">
                <div class="appointment-info">
                    <h4>Patient: ${appointment.patientName}</h4>
                    <div class="patient-details">
                        <p><strong>Patient Email:</strong> ${appointment.patientEmail || 'Not available'}</p>
                        <p><strong>Patient Phone:</strong> ${appointment.patientPhone || 'Not available'}</p>
                    </div>
                    <p><strong>Date & Time:</strong> ${new Date(appointment.appointmentDate).toLocaleString()}</p>
                    <p><strong>Type:</strong> ${appointment.appointmentType}</p>
                    <p><strong>Reason:</strong> ${appointment.reason || 'Not specified'}</p>
                    <p><strong>Status:</strong> <span class="status ${appointment.status.toLowerCase()}">${this.getStatusDisplay(appointment.status)}</span></p>
                </div>
                <div class="appointment-actions">
                    ${appointment.status === 'SCHEDULED' ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'CONFIRMED')" class="btn btn-success btn-sm">Approve</button>` : 
                        ''}
                    ${appointment.status === 'CONFIRMED' ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'COMPLETED')" class="btn btn-primary btn-sm">Complete</button>` : 
                        ''}
                    ${(appointment.status === 'SCHEDULED' || appointment.status === 'CONFIRMED') ? 
                        `<button onclick="app.updateAppointmentStatus('${appointment.id}', 'CANCELLED')" class="btn btn-danger btn-sm">Cancel</button>` : 
                        ''}
                </div>
            </div>
        `).join('');
    }

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
                const statusMessage = status === 'CONFIRMED' ? 'approved' : status.toLowerCase();
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
        if (confirm('Are you sure you want to cancel this appointment?')) {
            await this.updateAppointmentStatus(appointmentId, 'CANCELLED');
        }
    }

    // Appointment Methods
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
                    option.textContent = `${doctor.name} - ${doctor.specialization}`;
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

    renderPatientAppointments(appointments) {
        const container = document.getElementById('patient-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = '<p class="no-appointments">No appointments scheduled.</p>';
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="appointment-card">
                <div class="appointment-info">
                    <h4>Dr. ${appointment.doctorName}</h4>
                    <p><strong>Specialization:</strong> ${appointment.doctorSpecialization}</p>
                    <p><strong>Date & Time:</strong> ${new Date(appointment.appointmentDate).toLocaleString()}</p>
                    <p><strong>Type:</strong> ${appointment.appointmentType}</p>
                    <p><strong>Reason:</strong> ${appointment.reason || 'Not specified'}</p>
                    <p><strong>Status:</strong> <span class="status ${appointment.status.toLowerCase()}">${this.getStatusDisplay(appointment.status)}</span></p>
                    ${appointment.notes ? `<p><strong>Notes:</strong> ${appointment.notes}</p>` : ''}
                    ${appointment.diagnosis ? `<p><strong>Diagnosis:</strong> ${appointment.diagnosis}</p>` : ''}
                    ${appointment.treatmentPlan ? `<p><strong>Treatment Plan:</strong> ${appointment.treatmentPlan}</p>` : ''}
                </div>
                <div class="appointment-actions">
                    ${appointment.status === 'SCHEDULED' ? 
                        `<button onclick="app.cancelAppointment('${appointment.id}')" class="btn btn-danger btn-sm">Cancel</button>` : 
                        ''}
                </div>
            </div>
        `).join('');
    }

    renderPatientUpcomingAppointments(appointments) {
        const container = document.getElementById('patient-upcoming-appointments');
        if (!container) return;

        if (!appointments || appointments.length === 0) {
            container.innerHTML = '<p class="no-appointments">No upcoming appointments.</p>';
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="appointment-card">
                <div class="appointment-info">
                    <h4>Dr. ${appointment.doctorName}</h4>
                    <p><strong>Specialization:</strong> ${appointment.doctorSpecialization}</p>
                    <p><strong>Date & Time:</strong> ${new Date(appointment.appointmentDate).toLocaleString()}</p>
                    <p><strong>Type:</strong> ${appointment.appointmentType}</p>
                    <p><strong>Reason:</strong> ${appointment.reason || 'Not specified'}</p>
                    <p><strong>Status:</strong> <span class="status ${appointment.status.toLowerCase()}">${this.getStatusDisplay(appointment.status)}</span></p>
                </div>
                <div class="appointment-actions">
                    ${appointment.status === 'SCHEDULED' ? 
                        `<button onclick="app.cancelAppointment('${appointment.id}')" class="btn btn-danger btn-sm">Cancel</button>` : 
                        ''}
                </div>
            </div>
        `).join('');
    }

    getStatusDisplay(status) {
        const statusMap = {
            'SCHEDULED': 'Scheduled',
            'CONFIRMED': 'Confirmed by Doctor',
            'COMPLETED': 'Completed',
            'CANCELLED': 'Cancelled'
        };
        return statusMap[status] || status;
    }

    async cancelAppointment(appointmentId) {
        if (confirm('Are you sure you want to cancel this appointment?')) {
            await this.updateAppointmentStatus(appointmentId, 'CANCELLED');
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

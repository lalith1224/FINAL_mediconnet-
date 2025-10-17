package com.mediconnect.dto;

import com.mediconnect.entity.Appointment;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentBookingRequest {
    
    @NotNull
    private UUID doctorId;
    
    @NotNull
    private LocalDateTime appointmentDate;
    
    @NotNull
    private Appointment.AppointmentType appointmentType;
    
    private String reason;
    
    // Constructors
    public AppointmentBookingRequest() {}
    
    public AppointmentBookingRequest(UUID doctorId, LocalDateTime appointmentDate, 
                                   Appointment.AppointmentType appointmentType, String reason) {
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentType = appointmentType;
        this.reason = reason;
    }
    
    // Getters and Setters
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Appointment.AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(Appointment.AppointmentType appointmentType) { this.appointmentType = appointmentType; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

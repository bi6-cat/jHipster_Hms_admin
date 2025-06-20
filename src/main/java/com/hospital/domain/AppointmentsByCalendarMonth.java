package com.hospital.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments_by_calendar_month")
public class AppointmentsByCalendarMonth {

    @Id
    private Integer calendarMonth;

    private Integer totalAppointments;

    // Getters & Setters
    public Integer getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(Integer calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
}

package com.hospital.service;

import com.hospital.domain.AppointmentsByCalendarMonth;
import com.hospital.repository.AppointmentsByCalendarMonthRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final AppointmentsByCalendarMonthRepository calendarMonthRepo;

    public ReportService(AppointmentsByCalendarMonthRepository calendarMonthRepo) {
        this.calendarMonthRepo = calendarMonthRepo;
    }

    public List<AppointmentsByCalendarMonth> getCalendarMonthData() {
        return calendarMonthRepo.findAllSortedByMonth();
    }
}

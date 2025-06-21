package com.hospital.service;

import com.hospital.domain.AppointmentsByCalendarMonth;
import com.hospital.domain.DiseaseByGender;
import com.hospital.domain.RevenueByMonth;
import com.hospital.repository.AppointmentsByCalendarMonthRepository;
import com.hospital.repository.DiseaseByGenderRepository;
import com.hospital.repository.RevenueByMonthRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final AppointmentsByCalendarMonthRepository calendarMonthRepo;
    private final RevenueByMonthRepository revenueByMonthRepository;
    private final DiseaseByGenderRepository diseaseByGenderRepository;

    public ReportService(
        AppointmentsByCalendarMonthRepository calendarMonthRepo,
        RevenueByMonthRepository revenueByMonthRepository,
        DiseaseByGenderRepository diseaseByGenderRepository
    ) {
        this.calendarMonthRepo = calendarMonthRepo;
        this.revenueByMonthRepository = revenueByMonthRepository;
        this.diseaseByGenderRepository = diseaseByGenderRepository;
    }

    public List<AppointmentsByCalendarMonth> getCalendarMonthData() {
        return calendarMonthRepo.findAllSortedByMonth();
    }

    public List<RevenueByMonth> getRevenueByMonthData() {
        return revenueByMonthRepository.findAllOrderByYearAndMonth();
    }

    public List<DiseaseByGender> getDiseaseByGenderData() {
        return diseaseByGenderRepository.findAllOrderByDiseaseNameAndGender();
    }
}

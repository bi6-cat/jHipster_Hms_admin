package com.hospital.repository;

import com.hospital.domain.AppointmentsByCalendarMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentsByCalendarMonthRepository extends JpaRepository<AppointmentsByCalendarMonth, Integer> {
    @Query("SELECT a FROM AppointmentsByCalendarMonth a ORDER BY a.calendarMonth ASC")
    List<AppointmentsByCalendarMonth> findAllSortedByMonth(); // Sắp xếp theo tháng (calendarMonth)
}

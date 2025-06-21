package com.hospital.repository;

import com.hospital.domain.RevenueByMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RevenueByMonthRepository extends JpaRepository<RevenueByMonth, Long> {
    @Query("SELECT r FROM RevenueByMonth r ORDER BY r.year, r.month")
    List<RevenueByMonth> findAllOrderByYearAndMonth();
}

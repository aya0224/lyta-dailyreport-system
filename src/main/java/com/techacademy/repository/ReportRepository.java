package com.techacademy.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {


List<Report> findByEmployee(Employee employee);
boolean existsByEmployeeAndReportDate(Employee employee, LocalDate localDate);
}
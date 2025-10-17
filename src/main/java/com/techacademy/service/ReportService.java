package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

@Service


public class ReportService {

	private final ReportRepository reportRepository;


    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }

    //日報一覧
    public List<Report> findAll(){
    	return reportRepository.findAll();

    }

    // 自分の日報のみ取得（一般用）
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件取得
    public Report findById(Integer id) {
        return reportRepository.findById(id).orElse(null);
    }

 // 登録処理
    @Transactional
    public void save(Report report) {
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportRepository.save(report);
    }

    // 更新処理
    @Transactional
    public void update(Report report) {
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    // 論理削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        if (report != null) {
            report.setDeleteFlg(true);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
        }
    }

     //業務チェック（同じ社員・同じ日付が既に存在するか）
    public boolean existsSameDate(Employee employee, Report report) {
        return reportRepository.existsByEmployeeAndReportDate(employee, report.getReportDate());
    }

}


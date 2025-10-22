package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.entity.Report.Role;
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
    	return reportRepository.findByDeleteFlgFalse();

    }

    // 自分の日報のみ取得（一般用）
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployeeAndDeleteFlgFalse(employee);
    }

    // 1件取得
    public Report findById(Integer id) {
        return reportRepository.findById(id).orElse(null);
    }

    //同じ日付かチェックする
    public boolean existSameDate(Employee employee, Report report) {
	   return reportRepository.existsByEmployeeAndReportDate(employee, report.getReportDate());
    }




    // 登録処理
    @Transactional
    public ErrorKinds save(Report report) {

    	if(reportRepository.existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {
    		return ErrorKinds.DATECHECK_ERROR;
    	}

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportRepository.save(report);
		return ErrorKinds.SUCCESS;
    }


     // 日報削除
    @Transactional
    public ErrorKinds delete(Integer Id) {


        Report report = findById(Id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    //従業員更新処理
    @Transactional
    public ErrorKinds update(Report report) {

    	Report dbReport = findById(report.getId());
    	if (dbReport == null){
    		return ErrorKinds.SUCCESS;
    	}

    	if(report.getTitle() == null || report.getTitle().isEmpty()) {
    		return ErrorKinds.BLANK_ERROR;
    	}
    	if(report.getTitle().length()>100) {
    		return ErrorKinds.RANGECHECK_ERROR;
    	}
    	if(report.getContent() == null || report.getContent().isEmpty()) {
    		return ErrorKinds.BLANK_ERROR;
    	}
    	if(report.getContent().length()>600) {
    		return ErrorKinds.RANGECHECK_ERROR;
    	}



//リクエストされた情報を（ブラウザで操作された物）をエンティティに載せ替えている　その結果をリポジトリ（DBとJavaの橋渡し）に渡す

    	dbReport.setReportDate(report.getReportDate());
    	dbReport.setTitle(report.getTitle());
    	dbReport.setContent(report.getContent());
    	dbReport.setUpdatedAt(LocalDateTime.now());

    	reportRepository.save(dbReport);

    	return ErrorKinds.SUCCESS;
}

     //業務チェック（同じ社員・同じ日付が既に存在するか）
    public boolean existsSameDate(Employee employee, Report report) {
        return reportRepository.existsByEmployeeAndReportDate(employee, report.getReportDate());
    }

}


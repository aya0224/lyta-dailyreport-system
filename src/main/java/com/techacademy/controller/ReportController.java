package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.techacademy.repository.EmployeeRepository;
import com.techacademy.entity.Employee;

@Controller
@RequestMapping("reports")
public class ReportController {

	//このコントローラーが使うサービスのフィールド宣言
	private final ReportService reportService;

	//依存するオブジェクトを外から入れる　コンストラクタの注入
	@Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

	//日報一覧画面
	 @GetMapping("")
	    public String list(@AuthenticationPrincipal UserDetail userDetail,Model model) {
		 Employee loginUser = userDetail.getEmployee();
		 List<Report> reports;

		 if(loginUser.isAdmin()) {
			 reports = reportService.findAll();
		 }else {
			 reports = reportService.findByEmployee(loginUser);
		 }

		    model.addAttribute("reportList", reports);
	        model.addAttribute("listSize", reports.size());


	        return "reports/list";
	    }

	// 日報新規登録画面
	 @Autowired
	 private EmployeeRepository employeeRepository;

	    @GetMapping(value = "/add")
	    public String create(Model model) {
	        // ログイン中のユーザー名を取得（通常は従業員コードやログインID）
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String loginCode = auth.getName();

	        // ログインユーザーに対応するEmployeeをDBから取得

	        Employee employee = employeeRepository.findByCode(loginCode);

	        // モデルにセット
	        model.addAttribute("loginUser", employee);

	        // 空のReportを渡す
	        model.addAttribute("report", new Report());

	        return "reports/new";
	    }

	    // 日報新規登録処理
	    @PostMapping(value = "/add")
	    public String add(@AuthenticationPrincipal UserDetail userDetail,@Validated @ModelAttribute Report report,
                BindingResult result, Model model) {
            //ログイン中の従業員設定
	    	Employee employee = userDetail.getEmployee();
	        report.setEmployee(employee);

            if (result.hasErrors()) {
            	model.addAttribute("loginUser", userDetail.getEmployee());
            	model.addAttribute("report", report);
            	return "reports/new";
            }

            ErrorKinds saveResult =reportService.save(report);

            if(ErrorMessage.contains(saveResult)) {
            	model.addAttribute(ErrorMessage.getErrorName(saveResult),ErrorMessage.getErrorValue(saveResult));
            	model.addAttribute("loginUser", userDetail.getEmployee());
            	return "reports/new";


            }

	        return "redirect:/reports";
	    }

	  //詳細画面表示
	    @GetMapping(value = "/{id}/")
	    public String detail(@PathVariable("id") Integer id, Model model) {
	    	Report report = reportService.findById(id);

	    	model.addAttribute("report" , report);
	    	return "reports/detail";
	    }

	    //更新画面表示
	    @GetMapping("/{id}/update")
	    public String edit(@PathVariable("id") Integer id, Model model) {
	    	Report report = reportService.findById(id);

	    	model.addAttribute("report" , report);
	    	return "reports/update";
	    }

	    //更新処理
	    @PostMapping("/{id}/update")
	    public String update(@PathVariable Integer id, @Validated @ModelAttribute Report report, BindingResult result, @AuthenticationPrincipal UserDetail userDetail, Model model) {

	    if (result.hasErrors()) {
	    	report.setEmployee(userDetail.getEmployee());
	    	model.addAttribute("loginUser", userDetail.getEmployee());
	    	model.addAttribute("report",report);
	    	return "reports/update";
	    }

	    	report.setId(id);
	    	reportService.update(report);


	    return "redirect:/reports";

}

	    // 従業員削除処理
	    @PostMapping(value = "/{id}/delete")
	    public String delete(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

	        ErrorKinds result = reportService.delete(id);

	        if (ErrorMessage.contains(result)) {
	            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
	            model.addAttribute("employee", reportService.findById(id));
	            return detail(id, model);
	        }

	        return "redirect:/reports";
	    }


}
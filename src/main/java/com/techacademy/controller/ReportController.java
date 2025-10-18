package com.techacademy.controller;

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


	        model.addAttribute("listSize", reportService.findAll().size());
	        model.addAttribute("reportList", reportService.findAll());

	        return "reports/list";
	    }

	// 従業員新規登録画面
	 @Autowired
	 private EmployeeRepository employeeRepository;

	    @GetMapping(value = "/add")
	    public String showNewReportForm(Model model) {
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

	    // 従業員新規登録処理
	    @PostMapping(value = "/add")
	    public String add(@AuthenticationPrincipal UserDetail userDetail,@Validated @ModelAttribute Report report,
                BindingResult result, Model model) {

	    	Employee employee = userDetail.getEmployee();
	        report.setEmployee(employee);

	        /*入力チェック
	        if (reportService.existsSameDate(employee, report)) {
	            result.rejectValue("reportDate", null, "既に登録されている日付です");
	        }*/
            if(result.hasErrors()) {
            	model.addAttribute("loginUser", userDetail.getEmployee());
            	return "reports/new";
            }

            reportService.save(report);





	   /* ErrorKinds result =reportService.create(report, userDetail.getEmployee());
	    if()
	    */




	        return "redirect:/reports";
	    }





}
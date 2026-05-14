package com.workforcehub.controller;

import com.workforcehub.service.DashboardService;
import com.workforcehub.service.DepartmentService;
import com.workforcehub.service.EmployeeService;
import com.workforcehub.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final DashboardService dashboardService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final LeaveService leaveService;

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() { return "forgot-password"; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("dashboard", dashboardService.getDashboardData());
        model.addAttribute("username", user != null ? user.getUsername() : "Guest");
        return "dashboard";
    }

    @GetMapping("/employees")
    public String employees(Model model, @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {
        model.addAttribute("employees", employeeService.getAllEmployees(page, size, "id", "asc", search));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "employees";
    }

    @GetMapping("/employees/{id}")
    public String employeeProfile(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employee-profile";
    }

    @GetMapping("/employees/add")
    public String addEmployee(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employee-form";
    }

    @GetMapping("/employees/{id}/edit")
    public String editEmployee(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employee-form";
    }

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    @GetMapping("/leaves")
    public String leaves(Model model) {
        model.addAttribute("pendingLeaves", leaveService.getPendingRequests());
        return "leaves";
    }
}

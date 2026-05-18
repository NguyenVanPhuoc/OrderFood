package com.example.lesson3.controller;

import com.example.lesson3.request.DashboardStatsDTO;
import com.example.lesson3.service.DashboardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    public DashboardController(DashboardService dashboardService, ObjectMapper objectMapper) {
        this.dashboardService = dashboardService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String dashboard(Model model) throws JsonProcessingException {
        DashboardStatsDTO stats = dashboardService.getStats();
        model.addAttribute("stats", stats);
        // Truyền JSON để Chart.js đọc trực tiếp, tránh eval/XSS
        model.addAttribute("chartLabelsJson", objectMapper.writeValueAsString(stats.getChartLabels()));
        model.addAttribute("chartDataJson", objectMapper.writeValueAsString(stats.getChartOrderCounts()));
        model.addAttribute("topProductNamesJson", objectMapper.writeValueAsString(stats.getTopProductNames()));
        model.addAttribute("topProductQtysJson", objectMapper.writeValueAsString(stats.getTopProductQuantities()));
        model.addAttribute("contentPage", "/WEB-INF/views/admin/dashboard.jsp");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("currentPath", "/admin/dashboard");
        return "layouts/main";
    }
}

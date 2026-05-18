package com.example.lesson3.request;

import java.util.List;

public class DashboardStatsDTO {

    private long totalOrders;
    private long paidOrders;
    private long unpaidOrders;
    private double totalRevenue;
    private long totalUsers;
    private long totalStores;

    // Biểu đồ đơn hàng 7 ngày gần nhất
    private List<String> chartLabels;
    private List<Long> chartOrderCounts;

    // Top 5 sản phẩm bán chạy
    private List<String> topProductNames;
    private List<Long> topProductQuantities;

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getPaidOrders() { return paidOrders; }
    public void setPaidOrders(long paidOrders) { this.paidOrders = paidOrders; }

    public long getUnpaidOrders() { return unpaidOrders; }
    public void setUnpaidOrders(long unpaidOrders) { this.unpaidOrders = unpaidOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalStores() { return totalStores; }
    public void setTotalStores(long totalStores) { this.totalStores = totalStores; }

    public List<String> getChartLabels() { return chartLabels; }
    public void setChartLabels(List<String> chartLabels) { this.chartLabels = chartLabels; }

    public List<Long> getChartOrderCounts() { return chartOrderCounts; }
    public void setChartOrderCounts(List<Long> chartOrderCounts) { this.chartOrderCounts = chartOrderCounts; }

    public List<String> getTopProductNames() { return topProductNames; }
    public void setTopProductNames(List<String> topProductNames) { this.topProductNames = topProductNames; }

    public List<Long> getTopProductQuantities() { return topProductQuantities; }
    public void setTopProductQuantities(List<Long> topProductQuantities) { this.topProductQuantities = topProductQuantities; }
}

package jp.asatex.matianchi.social_insurance_backend_service.domain.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 社会保险金额查询结果DTO
 * 用于返回社会保险费用计算结果
 * 包含雇员和雇主各自承担的费用（各50%）
 */
public class SocialInsuranceDomainDto {

    /**
     * 雇员承担的费用
     */
    private CostDetail employeeCost;

    /**
     * 雇主承担的费用
     */
    private CostDetail employerCost;

    // 默认构造函数
    public SocialInsuranceDomainDto() {
    }

    // 全参构造函数
    public SocialInsuranceDomainDto(CostDetail employeeCost, CostDetail employerCost) {
        this.employeeCost = employeeCost;
        this.employerCost = employerCost;
    }

    // Getter和Setter方法
    public CostDetail getEmployeeCost() {
        return employeeCost;
    }

    public void setEmployeeCost(CostDetail employeeCost) {
        this.employeeCost = employeeCost;
    }

    public CostDetail getEmployerCost() {
        return employerCost;
    }

    public void setEmployerCost(CostDetail employerCost) {
        this.employerCost = employerCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialInsuranceDomainDto that = (SocialInsuranceDomainDto) o;
        return Objects.equals(employeeCost, that.employeeCost) &&
               Objects.equals(employerCost, that.employerCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeCost, employerCost);
    }

    @Override
    public String toString() {
        return "SocialInsuranceDomainDto{" +
               "employeeCost=" + employeeCost +
               ", employerCost=" + employerCost +
               '}';
    }

    /**
     * 费用明细结构体
     * 包含健康保险、介护保险、厚生年金的费用
     */
    public static class CostDetail {
        /**
         * 无介护健康保险金额
         */
        private BigDecimal healthCostWithNoCare;

        /**
         * 介护保险金额
         */
        private BigDecimal careCost;

        /**
         * 厚生年金金额
         */
        private BigDecimal pension;

        // 默认构造函数
        public CostDetail() {
        }

        // 全参构造函数
        public CostDetail(BigDecimal healthCostWithNoCare, BigDecimal careCost, BigDecimal pension) {
            this.healthCostWithNoCare = healthCostWithNoCare;
            this.careCost = careCost;
            this.pension = pension;
        }

        // Getter和Setter方法
        public BigDecimal getHealthCostWithNoCare() {
            return healthCostWithNoCare;
        }

        public void setHealthCostWithNoCare(BigDecimal healthCostWithNoCare) {
            this.healthCostWithNoCare = healthCostWithNoCare;
        }

        public BigDecimal getCareCost() {
            return careCost;
        }

        public void setCareCost(BigDecimal careCost) {
            this.careCost = careCost;
        }

        public BigDecimal getPension() {
            return pension;
        }

        public void setPension(BigDecimal pension) {
            this.pension = pension;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CostDetail that = (CostDetail) o;
            return Objects.equals(healthCostWithNoCare, that.healthCostWithNoCare) &&
                   Objects.equals(careCost, that.careCost) &&
                   Objects.equals(pension, that.pension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(healthCostWithNoCare, careCost, pension);
        }

        @Override
        public String toString() {
            return "CostDetail{" +
                   "healthCostWithNoCare=" + healthCostWithNoCare +
                   ", careCost=" + careCost +
                   ", pension=" + pension +
                   '}';
        }
    }
}


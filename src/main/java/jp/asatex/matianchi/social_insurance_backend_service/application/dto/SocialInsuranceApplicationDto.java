package jp.asatex.matianchi.social_insurance_backend_service.application.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 社会保险金额查询结果Application DTO
 * 用于Application层返回社会保险费用计算结果
 * 包含雇员和雇主各自承担的费用（各50%）
 */
public class SocialInsuranceApplicationDto {

    /**
     * 雇员承担的费用
     */
    private CostDetail employeeCost;

    /**
     * 雇主承担的费用
     */
    private CostDetail employerCost;

    // 默认构造函数
    public SocialInsuranceApplicationDto() {
    }

    // 全参构造函数
    public SocialInsuranceApplicationDto(CostDetail employeeCost, CostDetail employerCost) {
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
        SocialInsuranceApplicationDto that = (SocialInsuranceApplicationDto) o;
        return Objects.equals(employeeCost, that.employeeCost) &&
               Objects.equals(employerCost, that.employerCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeCost, employerCost);
    }

    @Override
    public String toString() {
        return "SocialInsuranceApplicationDto{" +
               "employeeCost=" + employeeCost +
               ", employerCost=" + employerCost +
               '}';
    }

    /**
     * 费用明细结构体
     * 包含健康保险、介护保险、厚生年金、源泉征收税、雇佣保险的费用
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

        /**
         * 源泉征收税金额（仅雇员负担）
         */
        private BigDecimal withholdingTax;

        /**
         * 雇佣保险金额
         */
        private BigDecimal employmentInsurance;

        // 默认构造函数
        public CostDetail() {
        }

        // 全参构造函数
        public CostDetail(BigDecimal healthCostWithNoCare, BigDecimal careCost, BigDecimal pension,
                         BigDecimal withholdingTax, BigDecimal employmentInsurance) {
            this.healthCostWithNoCare = healthCostWithNoCare;
            this.careCost = careCost;
            this.pension = pension;
            this.withholdingTax = withholdingTax;
            this.employmentInsurance = employmentInsurance;
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

        public BigDecimal getWithholdingTax() {
            return withholdingTax;
        }

        public void setWithholdingTax(BigDecimal withholdingTax) {
            this.withholdingTax = withholdingTax;
        }

        public BigDecimal getEmploymentInsurance() {
            return employmentInsurance;
        }

        public void setEmploymentInsurance(BigDecimal employmentInsurance) {
            this.employmentInsurance = employmentInsurance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CostDetail that = (CostDetail) o;
            return Objects.equals(healthCostWithNoCare, that.healthCostWithNoCare) &&
                   Objects.equals(careCost, that.careCost) &&
                   Objects.equals(pension, that.pension) &&
                   Objects.equals(withholdingTax, that.withholdingTax) &&
                   Objects.equals(employmentInsurance, that.employmentInsurance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(healthCostWithNoCare, careCost, pension, withholdingTax, employmentInsurance);
        }

        @Override
        public String toString() {
            return "CostDetail{" +
                   "healthCostWithNoCare=" + healthCostWithNoCare +
                   ", careCost=" + careCost +
                   ", pension=" + pension +
                   ", withholdingTax=" + withholdingTax +
                   ", employmentInsurance=" + employmentInsurance +
                   '}';
        }
    }
}


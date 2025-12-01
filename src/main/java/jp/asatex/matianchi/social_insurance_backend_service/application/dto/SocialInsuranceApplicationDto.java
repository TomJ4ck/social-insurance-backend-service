package jp.asatex.matianchi.social_insurance_backend_service.application.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 社会保险金额查询结果Application DTO
 * 用于Application层返回社会保险费用计算结果
 */
public class SocialInsuranceApplicationDto {

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
    public SocialInsuranceApplicationDto() {
    }

    // 全参构造函数
    public SocialInsuranceApplicationDto(BigDecimal healthCostWithNoCare, BigDecimal careCost, BigDecimal pension) {
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
        SocialInsuranceApplicationDto that = (SocialInsuranceApplicationDto) o;
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
        return "SocialInsuranceApplicationDto{" +
               "healthCostWithNoCare=" + healthCostWithNoCare +
               ", careCost=" + careCost +
               ", pension=" + pension +
               '}';
    }
}


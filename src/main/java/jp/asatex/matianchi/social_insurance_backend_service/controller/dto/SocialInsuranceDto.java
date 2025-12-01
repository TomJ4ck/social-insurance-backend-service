package jp.asatex.matianchi.social_insurance_backend_service.controller.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 社会保险金额查询结果Controller DTO
 * 用于Controller层返回社会保险费用计算结果
 */
public class SocialInsuranceDto {

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
    public SocialInsuranceDto() {
    }

    // 全参构造函数
    public SocialInsuranceDto(BigDecimal healthCostWithNoCare, BigDecimal careCost, BigDecimal pension) {
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
        SocialInsuranceDto that = (SocialInsuranceDto) o;
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
        return "SocialInsuranceDto{" +
               "healthCostWithNoCare=" + healthCostWithNoCare +
               ", careCost=" + careCost +
               ", pension=" + pension +
               '}';
    }
}


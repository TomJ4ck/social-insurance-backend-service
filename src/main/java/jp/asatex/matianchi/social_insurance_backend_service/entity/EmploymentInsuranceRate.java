package jp.asatex.matianchi.social_insurance_backend_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 雇佣保险费率实体类
 * 对应数据库表：employment_insurance_rate
 * 用于存储令和7年（2025年）度雇佣保险费率表数据
 */
@Table("employment_insurance_rate")
public class EmploymentInsuranceRate {

    @Id
    @Column("id")
    private Long id;

    @Column("business_type")
    private String businessType;

    @Column("employee_rate")
    private BigDecimal employeeRate;

    @Column("employer_unemployment_rate")
    private BigDecimal employerUnemploymentRate;

    @Column("employer_two_undertakings_rate")
    private BigDecimal employerTwoUndertakingsRate;

    @Column("total_rate")
    private BigDecimal totalRate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // 默认构造函数
    public EmploymentInsuranceRate() {
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getEmployeeRate() {
        return employeeRate;
    }

    public void setEmployeeRate(BigDecimal employeeRate) {
        this.employeeRate = employeeRate;
    }

    public BigDecimal getEmployerUnemploymentRate() {
        return employerUnemploymentRate;
    }

    public void setEmployerUnemploymentRate(BigDecimal employerUnemploymentRate) {
        this.employerUnemploymentRate = employerUnemploymentRate;
    }

    public BigDecimal getEmployerTwoUndertakingsRate() {
        return employerTwoUndertakingsRate;
    }

    public void setEmployerTwoUndertakingsRate(BigDecimal employerTwoUndertakingsRate) {
        this.employerTwoUndertakingsRate = employerTwoUndertakingsRate;
    }

    public BigDecimal getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(BigDecimal totalRate) {
        this.totalRate = totalRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmploymentInsuranceRate that = (EmploymentInsuranceRate) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(businessType, that.businessType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, businessType);
    }

    @Override
    public String toString() {
        return "EmploymentInsuranceRate{" +
               "id=" + id +
               ", businessType='" + businessType + '\'' +
               ", employeeRate=" + employeeRate +
               ", employerUnemploymentRate=" + employerUnemploymentRate +
               ", employerTwoUndertakingsRate=" + employerTwoUndertakingsRate +
               ", totalRate=" + totalRate +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}


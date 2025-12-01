package jp.asatex.matianchi.social_insurance_backend_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 保险费等级实体类
 * 对应数据库表：premium_bracket
 * 用于存储2025年神奈川县社会保险费等级表数据
 */
@Table("premium_bracket")
public class PremiumBracket {

    @Id
    @Column("id")
    private Long id;

    @Column("grade")
    private String grade;

    @Column("std_rem")
    private Integer stdRem;

    @Column("min_amount")
    private Integer minAmount;

    @Column("max_amount")
    private Integer maxAmount;

    @Column("health_no_care")
    private BigDecimal healthNoCare;

    @Column("health_care")
    private BigDecimal healthCare;

    @Column("pension")
    private BigDecimal pension;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // 默认构造函数
    public PremiumBracket() {
    }

    // 全参构造函数（用于创建新实体）
    public PremiumBracket(String grade, Integer stdRem, Integer minAmount, Integer maxAmount,
                          BigDecimal healthNoCare, BigDecimal healthCare, BigDecimal pension) {
        this.grade = grade;
        this.stdRem = stdRem;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.healthNoCare = healthNoCare;
        this.healthCare = healthCare;
        this.pension = pension;
    }

    // 包含ID的完整构造函数（用于从数据库读取）
    public PremiumBracket(Long id, String grade, Integer stdRem, Integer minAmount, Integer maxAmount,
                          BigDecimal healthNoCare, BigDecimal healthCare, BigDecimal pension,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.grade = grade;
        this.stdRem = stdRem;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.healthNoCare = healthNoCare;
        this.healthCare = healthCare;
        this.pension = pension;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getStdRem() {
        return stdRem;
    }

    public void setStdRem(Integer stdRem) {
        this.stdRem = stdRem;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getHealthNoCare() {
        return healthNoCare;
    }

    public void setHealthNoCare(BigDecimal healthNoCare) {
        this.healthNoCare = healthNoCare;
    }

    public BigDecimal getHealthCare() {
        return healthCare;
    }

    public void setHealthCare(BigDecimal healthCare) {
        this.healthCare = healthCare;
    }

    public BigDecimal getPension() {
        return pension;
    }

    public void setPension(BigDecimal pension) {
        this.pension = pension;
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
        PremiumBracket that = (PremiumBracket) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade);
    }

    @Override
    public String toString() {
        return "PremiumBracket{" +
               "id=" + id +
               ", grade='" + grade + '\'' +
               ", stdRem=" + stdRem +
               ", minAmount=" + minAmount +
               ", maxAmount=" + maxAmount +
               ", healthNoCare=" + healthNoCare +
               ", healthCare=" + healthCare +
               ", pension=" + pension +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}


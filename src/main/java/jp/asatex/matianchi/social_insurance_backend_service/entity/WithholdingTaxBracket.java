package jp.asatex.matianchi.social_insurance_backend_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 源泉征收税等级实体类
 * 对应数据库表：withholding_tax_bracket
 * 用于存储令和7年（2025年）度源泉征收税等级表数据
 */
@Table("withholding_tax_bracket")
public class WithholdingTaxBracket {

    @Id
    @Column("id")
    private Long id;

    @Column("min_amount")
    private Integer minAmount;

    @Column("max_amount")
    private Integer maxAmount;

    @Column("tax_amount_ko")
    private Integer taxAmountKo;

    @Column("tax_amount_otsu")
    private Integer taxAmountOtsu;

    @Column("calculation_formula")
    private String calculationFormula;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // 默认构造函数
    public WithholdingTaxBracket() {
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getTaxAmountKo() {
        return taxAmountKo;
    }

    public void setTaxAmountKo(Integer taxAmountKo) {
        this.taxAmountKo = taxAmountKo;
    }

    public Integer getTaxAmountOtsu() {
        return taxAmountOtsu;
    }

    public void setTaxAmountOtsu(Integer taxAmountOtsu) {
        this.taxAmountOtsu = taxAmountOtsu;
    }

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
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
        WithholdingTaxBracket that = (WithholdingTaxBracket) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "WithholdingTaxBracket{" +
               "id=" + id +
               ", minAmount=" + minAmount +
               ", maxAmount=" + maxAmount +
               ", taxAmountKo=" + taxAmountKo +
               ", taxAmountOtsu=" + taxAmountOtsu +
               ", calculationFormula='" + calculationFormula + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}


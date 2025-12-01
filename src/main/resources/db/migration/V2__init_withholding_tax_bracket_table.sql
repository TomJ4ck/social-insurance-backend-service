-- ===========================================
-- 令和7年（2025年）度源泉征收税等级表
-- 用于源泉征收税的计算
-- ===========================================

-- 创建源泉征收税等级表
CREATE TABLE withholding_tax_bracket (
    id BIGSERIAL PRIMARY KEY,
    min_amount INTEGER NOT NULL,
    max_amount INTEGER NOT NULL,
    tax_amount_ko INTEGER,
    tax_amount_otsu INTEGER,
    calculation_formula TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加表注释
COMMENT ON TABLE withholding_tax_bracket IS '令和7年（2025年）度源泉征收税等级表，用于源泉征收税的计算';
COMMENT ON COLUMN withholding_tax_bracket.min_amount IS '扣除社会保险费后的工资金额最小值';
COMMENT ON COLUMN withholding_tax_bracket.max_amount IS '扣除社会保险费后的工资金额最大值（999999999表示无上限）';
COMMENT ON COLUMN withholding_tax_bracket.tax_amount_ko IS '甲类税额（有配偶和抚养人的情况）';
COMMENT ON COLUMN withholding_tax_bracket.tax_amount_otsu IS '乙类税额（单身等情况）';
COMMENT ON COLUMN withholding_tax_bracket.calculation_formula IS '计算公式（当税额需要计算时使用）';

-- 创建索引
CREATE INDEX idx_withholding_tax_bracket_min_max ON withholding_tax_bracket(min_amount, max_amount);

-- 插入2025年度源泉征收税等级表数据
-- 注意：以下数据为示例，请根据实际的源泉征收税表数据更新
-- 88,000円未満的情况
INSERT INTO withholding_tax_bracket (min_amount, max_amount, tax_amount_ko, tax_amount_otsu, calculation_formula) VALUES
(0, 88000, 0, 0, NULL);

-- 88,000円以上但小于其他金额的情况
-- 由于PDF数据不完整，这里提供基础结构
-- 请根据实际的源泉征收税表补充完整数据

-- 示例：88,000円以上 100,000円未満（示例数据，请替换为实际数据）
-- INSERT INTO withholding_tax_bracket (min_amount, max_amount, tax_amount_ko, tax_amount_otsu, calculation_formula) VALUES
-- (88000, 100000, 0, 0, NULL);

-- 对于需要计算公式的情况（如PDF中提到的"その月の社会保険料等控除後の給与等の金額のうち2,170,000円を超える金額の40.84%に相当する金額を加算した金額"）
-- INSERT INTO withholding_tax_bracket (min_amount, max_amount, tax_amount_ko, tax_amount_otsu, calculation_formula) VALUES
-- (2170000, 2210000, 593340, NULL, '2,170,000円の場合の税額に、その月の社会保険料等控除後の給与等の金額のうち2,170,000円を超える金額の40.84%に相当する金額を加算した金額');

-- 3,500,000円を超える金額的情况
INSERT INTO withholding_tax_bracket (min_amount, max_amount, tax_amount_ko, tax_amount_otsu, calculation_formula) VALUES
(3500000, 999999999, NULL, NULL, '3,500,000円を超える金額の場合の特別計算式');

-- 创建触发器函数，用于自动更新 updated_at 字段
-- 注意：函数已在V1脚本中创建，这里不需要再次创建

-- 创建触发器
DROP TRIGGER IF EXISTS update_withholding_tax_bracket_updated_at ON withholding_tax_bracket;
CREATE TRIGGER update_withholding_tax_bracket_updated_at
    BEFORE UPDATE ON withholding_tax_bracket
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


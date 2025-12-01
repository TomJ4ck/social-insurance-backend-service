-- ===========================================
-- 令和7年（2025年）度雇佣保险费率表
-- 用于雇佣保险的计算
-- ===========================================

-- 创建雇佣保险费率表
CREATE TABLE employment_insurance_rate (
    id BIGSERIAL PRIMARY KEY,
    business_type VARCHAR(50) NOT NULL UNIQUE,
    employee_rate NUMERIC(5, 3) NOT NULL,
    employer_unemployment_rate NUMERIC(5, 3) NOT NULL,
    employer_two_undertakings_rate NUMERIC(5, 3) NOT NULL,
    total_rate NUMERIC(5, 3) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加表注释
COMMENT ON TABLE employment_insurance_rate IS '令和7年（2025年）度雇佣保险费率表，用于雇佣保险的计算';
COMMENT ON COLUMN employment_insurance_rate.business_type IS '事业类型（一般の事業、農林水産・清酒製造の事業、建設の事業）';
COMMENT ON COLUMN employment_insurance_rate.employee_rate IS '劳动者负担率（失業等給付・育児休業給付の保険料率のみ），单位：千分比';
COMMENT ON COLUMN employment_insurance_rate.employer_unemployment_rate IS '事业主负担的失業等給付・育児休業給付の保険料率，单位：千分比';
COMMENT ON COLUMN employment_insurance_rate.employer_two_undertakings_rate IS '事业主负担的雇用保険二事業の保険料率，单位：千分比';
COMMENT ON COLUMN employment_insurance_rate.total_rate IS '合计雇佣保险费率，单位：千分比';

-- 创建索引
CREATE INDEX idx_employment_insurance_rate_business_type ON employment_insurance_rate(business_type);

-- 插入2025年度雇佣保险费率表数据
INSERT INTO employment_insurance_rate (business_type, employee_rate, employer_unemployment_rate, employer_two_undertakings_rate, total_rate) VALUES
('一般の事業', 5.5, 5.5, 3.5, 14.5),
('農林水産・清酒製造の事業', 6.5, 6.5, 3.5, 16.5),
('建設の事業', 6.5, 6.5, 4.5, 17.5);

-- 创建触发器函数，用于自动更新 updated_at 字段
-- 注意：函数已在V1脚本中创建，这里不需要再次创建

-- 创建触发器
DROP TRIGGER IF EXISTS update_employment_insurance_rate_updated_at ON employment_insurance_rate;
CREATE TRIGGER update_employment_insurance_rate_updated_at
    BEFORE UPDATE ON employment_insurance_rate
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


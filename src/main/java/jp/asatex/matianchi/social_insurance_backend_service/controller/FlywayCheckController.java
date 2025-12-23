package jp.asatex.matianchi.social_insurance_backend_service.controller;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/check")
public class FlywayCheckController {
    private final DatabaseClient databaseClient;

    public FlywayCheckController(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/flyway-status")
    public Mono<Map<String, Object>> checkFlywayStatus() {
        return databaseClient.sql("SELECT version, description, type, installed_on, success FROM flyway_schema_history ORDER BY installed_rank")
                .map((row, metadata) -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("version", row.get("version"));
                    record.put("description", row.get("description"));
                    record.put("type", row.get("type"));
                    record.put("installed_on", row.get("installed_on"));
                    record.put("success", row.get("success"));
                    return record;
                })
                .all()
                .collectList()
                .map(records -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("migrations", records);
                    return result;
                })
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("error", e.getMessage());
                    return Mono.just(result);
                });
    }

    @GetMapping("/table-exists")
    public Mono<Map<String, Object>> checkTableExists() {
        return databaseClient.sql("SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'withholding_tax_bracket')")
                .map((row, metadata) -> row.get(0, Boolean.class))
                .first()
                .map(exists -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("table_exists", exists);
                    if (exists) {
                        return databaseClient.sql("SELECT COUNT(*) as count FROM withholding_tax_bracket")
                                .map((row, metadata) -> row.get("count", Long.class))
                                .first()
                                .map(count -> {
                                    result.put("record_count", count);
                                    return result;
                                });
                    }
                    return Mono.just(result);
                })
                .flatMap(mono -> mono)
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("error", e.getMessage());
                    result.put("table_exists", false);
                    return Mono.just(result);
                });
    }

    @GetMapping("/clean-v2-v4")
    public Mono<Map<String, Object>> cleanV2AndV4() {
        return databaseClient.sql("DELETE FROM flyway_schema_history WHERE version IN ('2', '4')")
                .fetch()
                .rowsUpdated()
                .map(rows -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "已删除 V2 和 V4 迁移记录，共删除 " + rows + " 条记录");
                    result.put("next_step", "请重新启动应用程序以触发 Flyway 重新执行 V2 迁移");
                    return result;
                })
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", e.getMessage());
                    return Mono.just(result);
                });
    }

    @GetMapping("/clean-all-migrations")
    public Mono<Map<String, Object>> cleanAllMigrations() {
        // 先删除所有表
        return databaseClient.sql("DROP TABLE IF EXISTS withholding_tax_bracket CASCADE")
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("DROP TABLE IF EXISTS employment_insurance_rate CASCADE")
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("DROP TABLE IF EXISTS premium_bracket CASCADE")
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("DELETE FROM flyway_schema_history")
                        .fetch()
                        .rowsUpdated())
                .map(rows -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "已清理所有迁移历史和表");
                    result.put("next_step", "请重新启动应用程序以触发 Flyway 重新执行所有迁移");
                    return result;
                })
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", e.getMessage());
                    return Mono.just(result);
                });
    }

    @GetMapping("/execute-v2-manually")
    public Mono<Map<String, Object>> executeV2Manually() {
        // 先检查表是否已存在
        return databaseClient.sql("SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'withholding_tax_bracket')")
                .map((row, metadata) -> row.get(0, Boolean.class))
                .first()
                .flatMap(exists -> {
                    if (exists) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "表已存在，无需执行 V2 迁移");
                        return Mono.just(result);
                    }
                    
                    // 执行 V2 迁移：创建表
                    return databaseClient.sql("""
                        CREATE TABLE withholding_tax_bracket (
                            id BIGSERIAL PRIMARY KEY,
                            min_amount INTEGER NOT NULL,
                            max_amount INTEGER NOT NULL,
                            tax_amount_ko INTEGER,
                            tax_amount_otsu INTEGER,
                            calculation_formula TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                        """)
                        .fetch()
                        .rowsUpdated()
                        .then(databaseClient.sql("""
                            COMMENT ON TABLE withholding_tax_bracket IS '令和7年（2025年）度源泉征收税等级表，用于源泉征收税的计算';
                            COMMENT ON COLUMN withholding_tax_bracket.min_amount IS '扣除社会保险费后的工资金额最小值';
                            COMMENT ON COLUMN withholding_tax_bracket.max_amount IS '扣除社会保险费后的工资金额最大值（999999999表示无上限）';
                            COMMENT ON COLUMN withholding_tax_bracket.tax_amount_ko IS '甲类税额（有配偶和抚养人的情况）';
                            COMMENT ON COLUMN withholding_tax_bracket.tax_amount_otsu IS '乙类税额（单身等情况）';
                            COMMENT ON COLUMN withholding_tax_bracket.calculation_formula IS '计算公式（当税额需要计算时使用）';
                            """)
                            .fetch()
                            .rowsUpdated())
                        .then(databaseClient.sql("CREATE INDEX idx_withholding_tax_bracket_min_max ON withholding_tax_bracket(min_amount, max_amount)")
                            .fetch()
                            .rowsUpdated())
                        .then(databaseClient.sql("""
                            INSERT INTO withholding_tax_bracket (min_amount, max_amount, tax_amount_ko, tax_amount_otsu, calculation_formula) VALUES
                            (0, 88000, 0, 0, NULL),
                            (88000, 90000, 0, 0, NULL),
                            (90000, 92000, 0, 0, NULL),
                            (92000, 94000, 0, 0, NULL),
                            (94000, 96000, 0, 0, NULL),
                            (96000, 98000, 0, 0, NULL),
                            (98000, 100000, 0, 0, NULL),
                            (100000, 105000, 0, 0, NULL),
                            (105000, 110000, 0, 0, NULL),
                            (110000, 115000, 0, 0, NULL),
                            (115000, 120000, 0, 0, NULL),
                            (120000, 125000, 0, 0, NULL),
                            (125000, 130000, 0, 0, NULL),
                            (130000, 135000, 0, 0, NULL),
                            (135000, 140000, 0, 0, NULL),
                            (140000, 145000, 0, 0, NULL),
                            (145000, 150000, 0, 0, NULL),
                            (150000, 155000, 0, 0, NULL),
                            (155000, 160000, 0, 0, NULL),
                            (160000, 165000, 0, 0, NULL),
                            (165000, 170000, 0, 0, NULL),
                            (170000, 175000, 0, 0, NULL),
                            (175000, 180000, 0, 0, NULL),
                            (180000, 185000, 0, 0, NULL),
                            (185000, 190000, 0, 0, NULL),
                            (190000, 195000, 0, 0, NULL),
                            (195000, 200000, 0, 0, NULL),
                            (200000, 205000, 0, 0, NULL),
                            (205000, 210000, 0, 0, NULL),
                            (210000, 215000, 0, 0, NULL),
                            (215000, 220000, 0, 0, NULL),
                            (220000, 225000, 0, 0, NULL),
                            (225000, 230000, 0, 0, NULL),
                            (230000, 235000, 0, 0, NULL),
                            (235000, 240000, 0, 0, NULL),
                            (240000, 245000, 0, 0, NULL),
                            (245000, 250000, 0, 0, NULL),
                            (250000, 255000, 0, 0, NULL),
                            (255000, 260000, 6640, 0, NULL),
                            (260000, 265000, 6640, 0, NULL),
                            (265000, 270000, 6640, 0, NULL),
                            (270000, 275000, 6640, 0, NULL),
                            (275000, 280000, 6640, 0, NULL),
                            (280000, 285000, 6640, 0, NULL),
                            (285000, 290000, 6640, 0, NULL),
                            (290000, 295000, 6640, 0, NULL),
                            (295000, 300000, 6640, 0, NULL),
                            (300000, 310000, 6640, 0, NULL),
                            (310000, 320000, 6640, 0, NULL),
                            (320000, 330000, 6640, 0, NULL),
                            (330000, 340000, 6640, 0, NULL),
                            (340000, 350000, 6640, 0, NULL),
                            (350000, 400000, 6640, 0, NULL),
                            (400000, 450000, 6640, 0, NULL),
                            (450000, 500000, 6640, 0, NULL),
                            (500000, 600000, 6640, 0, NULL),
                            (600000, 700000, 6640, 0, NULL),
                            (700000, 800000, 6640, 0, NULL),
                            (800000, 900000, 6640, 0, NULL),
                            (900000, 1000000, 6640, 0, NULL),
                            (1000000, 1200000, 6640, 0, NULL),
                            (1200000, 1400000, 6640, 0, NULL),
                            (1400000, 1600000, 6640, 0, NULL),
                            (1600000, 1800000, 6640, 0, NULL),
                            (1800000, 2000000, 6640, 0, NULL),
                            (2000000, 2100000, 6640, 0, NULL),
                            (2100000, 2170000, 6640, 0, NULL),
                            (2170000, 2210000, 593340, NULL, 'Formula for 2170000-2210000: Add 40.84% of amount exceeding 2,170,000 to base tax'),
                            (2210000, 2250000, 615120, NULL, 'Formula for 2210000-2250000: Add 40.84% of amount exceeding 2,210,000 to base tax'),
                            (2250000, 3500000, NULL, NULL, 'Formula for 2250000-3500000: Add 40.84% of amount exceeding 2,250,000 to base tax'),
                            (3500000, 999999999, NULL, NULL, 'Special formula for amounts exceeding 3,500,000')
                            """)
                            .fetch()
                            .rowsUpdated())
                        .then(databaseClient.sql("""
                            DROP TRIGGER IF EXISTS update_withholding_tax_bracket_updated_at ON withholding_tax_bracket;
                            CREATE TRIGGER update_withholding_tax_bracket_updated_at
                                BEFORE UPDATE ON withholding_tax_bracket
                                FOR EACH ROW
                                EXECUTE FUNCTION update_updated_at_column()
                            """)
                            .fetch()
                            .rowsUpdated())
                        .then(databaseClient.sql("""
                            INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
                            VALUES (
                                (SELECT COALESCE(MAX(installed_rank), 0) + 1 FROM flyway_schema_history),
                                '2',
                                'init withholding tax bracket table',
                                'SQL',
                                'V2__init_withholding_tax_bracket_table.sql',
                                0,
                                'manual',
                                CURRENT_TIMESTAMP,
                                0,
                                true
                            )
                            """)
                            .fetch()
                            .rowsUpdated())
                        .map(rows -> {
                            Map<String, Object> result = new HashMap<>();
                            result.put("success", true);
                            result.put("message", "V2 迁移已手动执行完成");
                            return result;
                        });
                })
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", e.getMessage());
                    return Mono.just(result);
                });
    }
}


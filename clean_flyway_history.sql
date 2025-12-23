-- 清理 Flyway 迁移历史中的 V2 和 V4 记录
-- 这样 Flyway 会重新执行 V2 迁移

DELETE FROM flyway_schema_history WHERE version IN ('2', '4');

-- 验证删除结果
SELECT version, description, installed_on FROM flyway_schema_history ORDER BY installed_rank;


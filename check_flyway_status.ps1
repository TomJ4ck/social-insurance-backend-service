# 检查 Flyway 迁移状态
$env:PGPASSWORD = "local"
$dbName = "social_insurance"
$dbUser = "db_user"
$dbHost = "localhost"
$dbPort = "5433"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "检查 Flyway 迁移历史" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Flyway 迁移历史
Write-Host "Flyway 迁移历史：" -ForegroundColor Yellow
& psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT version, description, type, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;" 2>&1

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "检查 withholding_tax_bracket 表" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查表是否存在
$tableCheck = & psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'withholding_tax_bracket');" -t 2>&1
Write-Host "表是否存在: $tableCheck" -ForegroundColor $(if ($tableCheck -match "t") { "Green" } else { "Red" })

if ($tableCheck -match "t") {
    Write-Host ""
    Write-Host "表中的记录数：" -ForegroundColor Yellow
    & psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "SELECT COUNT(*) FROM withholding_tax_bracket;" -t 2>&1
}


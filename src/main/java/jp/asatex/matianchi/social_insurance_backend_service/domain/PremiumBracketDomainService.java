package jp.asatex.matianchi.social_insurance_backend_service.domain;

import jp.asatex.matianchi.social_insurance_backend_service.domain.dto.SocialInsuranceDomainDto;
import jp.asatex.matianchi.social_insurance_backend_service.entity.EmploymentInsuranceRate;
import jp.asatex.matianchi.social_insurance_backend_service.entity.PremiumBracket;
import jp.asatex.matianchi.social_insurance_backend_service.entity.WithholdingTaxBracket;
import jp.asatex.matianchi.social_insurance_backend_service.repository.EmploymentInsuranceRateRepository;
import jp.asatex.matianchi.social_insurance_backend_service.repository.PremiumBracketRepository;
import jp.asatex.matianchi.social_insurance_backend_service.repository.WithholdingTaxBracketRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 保险费等级Domain Service
 * 提供业务逻辑处理和流式编程风格的CRUD操作
 */
@Service
public class PremiumBracketDomainService {

    private final PremiumBracketRepository repository;
    private final WithholdingTaxBracketRepository withholdingTaxRepository;
    private final EmploymentInsuranceRateRepository employmentInsuranceRepository;

    public PremiumBracketDomainService(
            PremiumBracketRepository repository,
            WithholdingTaxBracketRepository withholdingTaxRepository,
            EmploymentInsuranceRateRepository employmentInsuranceRepository) {
        this.repository = repository;
        this.withholdingTaxRepository = withholdingTaxRepository;
        this.employmentInsuranceRepository = employmentInsuranceRepository;
    }

    /**
     * 查询社会保险金额
     * 根据月薪和年龄计算社会保险费用
     * 雇员和雇主各承担50%的费用
     * 包含健康保险、介护保险、厚生年金、源泉征收税、雇佣保险
     * 
     * @param monthlySalary 月薪
     * @param age 年龄
     * @param businessType 事业类型（可选，默认为"一般の事業"）
     * @return Mono包装的SocialInsuranceDomainDto对象
     */
    public Mono<SocialInsuranceDomainDto> socialInsuranceQuery(Integer monthlySalary, Integer age, String businessType) {
        // 默认使用"一般の事業"
        String finalBusinessType = (businessType != null && !businessType.isEmpty()) 
                ? businessType 
                : "一般の事業";
        
        // 查询社会保险费等级
        Mono<PremiumBracket> bracketMono = repository.findByAmount(monthlySalary)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "未找到月薪 " + monthlySalary + " 对应的保险费等级记录")));
        
        // 计算扣除社会保险费后的工资金额（用于源泉征收税计算）
        // 注意：源泉征收税应该基于扣除雇员承担的社会保险费后的工资金额
        Mono<Integer> salaryAfterSocialInsuranceMono = bracketMono
                .map(bracket -> {
                    // 计算总的社会保险费（健康保险 + 介护保险 + 厚生年金）
                    BigDecimal totalHealthCostWithNoCare = bracket.getHealthNoCare();
                    BigDecimal totalCareCost = (age < 40) ? BigDecimal.ZERO : bracket.getHealthCare().subtract(bracket.getHealthNoCare());
                    BigDecimal totalPension = bracket.getPension();
                    
                    // 雇员承担的社会保险费（总额的50%）
                    BigDecimal half = new BigDecimal("0.5");
                    BigDecimal employeeHealthCost = totalHealthCostWithNoCare.multiply(half);
                    BigDecimal employeeCareCost = totalCareCost.multiply(half);
                    BigDecimal employeePension = totalPension.multiply(half);
                    
                    // 扣除社会保险费后的工资金额 = 月薪 - 雇员承担的健康保险 - 雇员承担的介护保险 - 雇员承担的厚生年金
                    BigDecimal employeeSocialInsurance = employeeHealthCost.add(employeeCareCost).add(employeePension);
                    return monthlySalary - employeeSocialInsurance.intValue();
                });
        
        // 查询源泉征收税等级
        Mono<WithholdingTaxBracket> withholdingTaxMono = salaryAfterSocialInsuranceMono
                .flatMap(salaryAfter -> withholdingTaxRepository.findByAmount(salaryAfter)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                "未找到扣除社会保险费后工资金额 " + salaryAfter + " 对应的源泉征收税等级记录"))));
        
        // 查询雇佣保险费率
        Mono<EmploymentInsuranceRate> employmentInsuranceMono = employmentInsuranceRepository
                .findByBusinessType(finalBusinessType)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "未找到事业类型 " + finalBusinessType + " 对应的雇佣保险费率记录")));
        
        // 组合所有查询结果
        return Mono.zip(bracketMono, withholdingTaxMono, employmentInsuranceMono, salaryAfterSocialInsuranceMono)
                .map(tuple -> {
                    PremiumBracket bracket = tuple.getT1();
                    WithholdingTaxBracket withholdingTaxBracket = tuple.getT2();
                    EmploymentInsuranceRate employmentInsuranceRate = tuple.getT3();
                    Integer salaryAfterSocialInsurance = tuple.getT4();
                    
                    // 计算总费用
                    // 无介护健康保险金额
                    BigDecimal totalHealthCostWithNoCare = bracket.getHealthNoCare();
                    
                    // 介护保险金额计算
                    BigDecimal totalCareCost;
                    if (age < 40) {
                        // 年龄小于40岁，介护保险金额为0
                        totalCareCost = BigDecimal.ZERO;
                    } else {
                        // 年龄大于等于40岁，介护保险金额 = 有介护健康保险金额 - 无介护健康保险金额
                        totalCareCost = bracket.getHealthCare().subtract(bracket.getHealthNoCare());
                    }
                    
                    // 厚生年金金额
                    BigDecimal totalPension = bracket.getPension();
                    
                    // 源泉征收税金额（仅雇员负担，使用甲类税额）
                    BigDecimal withholdingTax = withholdingTaxBracket.getTaxAmountKo() != null
                            ? new BigDecimal(withholdingTaxBracket.getTaxAmountKo())
                            : BigDecimal.ZERO;
                    
                    // 雇佣保险金额计算
                    // 雇员负担 = 月薪 × 雇员费率 / 1000
                    BigDecimal employeeEmploymentInsurance = new BigDecimal(monthlySalary)
                            .multiply(employmentInsuranceRate.getEmployeeRate())
                            .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
                    
                    // 雇主负担 = 月薪 × (失業等給付率 + 雇用保険二事業率) / 1000
                    BigDecimal employerEmploymentInsurance = new BigDecimal(monthlySalary)
                            .multiply(employmentInsuranceRate.getEmployerUnemploymentRate()
                                    .add(employmentInsuranceRate.getEmployerTwoUndertakingsRate()))
                            .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
                    
                    // 雇员和雇主各承担50%的社会保险费
                    BigDecimal half = new BigDecimal("0.5");
                    
                    // 雇员承担的费用（50%的社会保险 + 源泉征收税 + 雇佣保险）
                    SocialInsuranceDomainDto.CostDetail employeeCost = new SocialInsuranceDomainDto.CostDetail(
                            totalHealthCostWithNoCare.multiply(half),
                            totalCareCost.multiply(half),
                            totalPension.multiply(half),
                            withholdingTax, // 源泉征收税仅雇员负担
                            employeeEmploymentInsurance // 雇员负担的雇佣保险
                    );
                    
                    // 雇主承担的费用（50%的社会保险 + 雇佣保险）
                    SocialInsuranceDomainDto.CostDetail employerCost = new SocialInsuranceDomainDto.CostDetail(
                            totalHealthCostWithNoCare.multiply(half),
                            totalCareCost.multiply(half),
                            totalPension.multiply(half),
                            BigDecimal.ZERO, // 雇主不负担源泉征收税
                            employerEmploymentInsurance // 雇主负担的雇佣保险
                    );
                    
                    return new SocialInsuranceDomainDto(employeeCost, employerCost);
                });
    }
    
    /**
     * 查询社会保险金额（重载方法，使用默认事业类型）
     * 
     * @param monthlySalary 月薪
     * @param age 年龄
     * @return Mono包装的SocialInsuranceDomainDto对象
     */
    public Mono<SocialInsuranceDomainDto> socialInsuranceQuery(Integer monthlySalary, Integer age) {
        return socialInsuranceQuery(monthlySalary, age, null);
    }

    /**
     * 根据ID查找保险费等级记录
     * 
     * @param id 记录ID
     * @return Mono包装的PremiumBracket对象
     */
    public Mono<PremiumBracket> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("未找到ID为 " + id + " 的记录")));
    }

    /**
     * 查找所有保险费等级记录
     * 
     * @return Flux包装的PremiumBracket对象流
     */
    public Flux<PremiumBracket> findAll() {
        return repository.findAll()
                .sort((b1, b2) -> Integer.compare(b1.getStdRem(), b2.getStdRem()));
    }

    /**
     * 根据等级查找保险费等级记录
     * 
     * @param grade 等级
     * @return Mono包装的PremiumBracket对象
     */
    public Mono<PremiumBracket> findByGrade(String grade) {
        return repository.findByGrade(grade)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("未找到等级为 " + grade + " 的记录")));
    }

    /**
     * 保存保险费等级记录（新增或更新）
     * 
     * @param bracket 保险费等级实体
     * @return Mono包装的已保存的PremiumBracket对象
     */
    public Mono<PremiumBracket> save(PremiumBracket bracket) {
        return repository.save(bracket)
                .doOnNext(saved -> {
                    // 可以在这里添加业务逻辑，如日志记录、事件发布等
                });
    }

    /**
     * 批量保存保险费等级记录
     * 
     * @param brackets 保险费等级实体流
     * @return Flux包装的已保存的PremiumBracket对象流
     */
    public Flux<PremiumBracket> saveAll(Flux<PremiumBracket> brackets) {
        return repository.saveAll(brackets)
                .doOnNext(saved -> {
                    // 可以在这里添加业务逻辑
                });
    }

    /**
     * 根据ID删除保险费等级记录
     * 
     * @param id 记录ID
     * @return Mono<Void>
     */
    public Mono<Void> deleteById(Long id) {
        return repository.findById(id)
                .flatMap(bracket -> repository.deleteById(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("未找到ID为 " + id + " 的记录，无法删除")));
    }

    /**
     * 删除所有保险费等级记录
     * 
     * @return Mono<Void>
     */
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    /**
     * 检查记录是否存在
     * 
     * @param id 记录ID
     * @return Mono包装的Boolean值
     */
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * 统计记录总数
     * 
     * @return Mono包装的Long值
     */
    public Mono<Long> count() {
        return repository.count();
    }
}


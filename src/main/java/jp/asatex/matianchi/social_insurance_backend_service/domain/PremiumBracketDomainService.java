package jp.asatex.matianchi.social_insurance_backend_service.domain;

import jp.asatex.matianchi.social_insurance_backend_service.domain.dto.SocialInsuranceDomainDto;
import jp.asatex.matianchi.social_insurance_backend_service.entity.PremiumBracket;
import jp.asatex.matianchi.social_insurance_backend_service.repository.PremiumBracketRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * 保险费等级Domain Service
 * 提供业务逻辑处理和流式编程风格的CRUD操作
 */
@Service
public class PremiumBracketDomainService {

    private final PremiumBracketRepository repository;

    public PremiumBracketDomainService(PremiumBracketRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询社会保险金额
     * 根据月薪和年龄计算社会保险费用
     * 雇员和雇主各承担50%的费用
     * 
     * @param monthlySalary 月薪
     * @param age 年龄
     * @return Mono包装的SocialInsuranceDomainDto对象
     */
    public Mono<SocialInsuranceDomainDto> socialInsuranceQuery(Integer monthlySalary, Integer age) {
        return repository.findByAmount(monthlySalary)
                .map(bracket -> {
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
                    
                    // 雇员和雇主各承担50%
                    BigDecimal half = new BigDecimal("0.5");
                    
                    // 雇员承担的费用（50%）
                    SocialInsuranceDomainDto.CostDetail employeeCost = new SocialInsuranceDomainDto.CostDetail(
                            totalHealthCostWithNoCare.multiply(half),
                            totalCareCost.multiply(half),
                            totalPension.multiply(half)
                    );
                    
                    // 雇主承担的费用（50%）
                    SocialInsuranceDomainDto.CostDetail employerCost = new SocialInsuranceDomainDto.CostDetail(
                            totalHealthCostWithNoCare.multiply(half),
                            totalCareCost.multiply(half),
                            totalPension.multiply(half)
                    );
                    
                    return new SocialInsuranceDomainDto(employeeCost, employerCost);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "未找到月薪 " + monthlySalary + " 对应的保险费等级记录")));
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


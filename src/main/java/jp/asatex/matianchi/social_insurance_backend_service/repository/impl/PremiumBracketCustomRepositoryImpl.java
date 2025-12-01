package jp.asatex.matianchi.social_insurance_backend_service.repository.impl;

import jp.asatex.matianchi.social_insurance_backend_service.entity.PremiumBracket;
import jp.asatex.matianchi.social_insurance_backend_service.repository.PremiumBracketCustomRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 * 保险费等级自定义Repository实现类
 * 使用R2dbcEntityTemplate实现流式编程风格的复杂查询
 */
@Repository
public class PremiumBracketCustomRepositoryImpl implements PremiumBracketCustomRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public PremiumBracketCustomRepositoryImpl(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    @Override
    public Flux<PremiumBracket> findByAmountRange(Integer minAmount, Integer maxAmount) {
        return r2dbcEntityTemplate
                .select(PremiumBracket.class)
                .matching(Query.query(
                        where("min_amount").lessThanOrEquals(maxAmount)
                                .and("max_amount").greaterThanOrEquals(minAmount)
                ))
                .all()
                .sort((b1, b2) -> Integer.compare(b1.getStdRem(), b2.getStdRem()));
    }

    @Override
    public Flux<PremiumBracket> findApplicablePensionBrackets() {
        return r2dbcEntityTemplate
                .select(PremiumBracket.class)
                .matching(Query.query(
                        where("pension").greaterThan(BigDecimal.ZERO)
                ))
                .all()
                .sort((b1, b2) -> Integer.compare(b1.getStdRem(), b2.getStdRem()));
    }

    @Override
    public Flux<PremiumBracket> findByStdRemRange(Integer minStdRem, Integer maxStdRem) {
        return r2dbcEntityTemplate
                .select(PremiumBracket.class)
                .matching(Query.query(
                        where("std_rem").greaterThanOrEquals(minStdRem)
                                .and("std_rem").lessThanOrEquals(maxStdRem)
                ))
                .all()
                .sort((b1, b2) -> Integer.compare(b1.getStdRem(), b2.getStdRem()));
    }

    @Override
    public Flux<PremiumBracket> saveAll(Flux<PremiumBracket> brackets) {
        return brackets
                .map(bracket -> {
                    // 创建新对象以确保不修改原始对象
                    PremiumBracket newBracket = new PremiumBracket(
                            bracket.getGrade(),
                            bracket.getStdRem(),
                            bracket.getMinAmount(),
                            bracket.getMaxAmount(),
                            bracket.getHealthNoCare(),
                            bracket.getHealthCare(),
                            bracket.getPension()
                    );
                    return newBracket;
                })
                .flatMap(bracket -> r2dbcEntityTemplate.insert(PremiumBracket.class)
                        .using(bracket)
                        .cast(PremiumBracket.class))
                .onErrorContinue((throwable, obj) -> {
                    // 错误处理：记录日志并继续处理下一个
                    System.err.println("保存保险费等级记录时发生错误: " + throwable.getMessage());
                });
    }

    @Override
    public Mono<Boolean> existsByGrade(String grade) {
        return r2dbcEntityTemplate
                .select(PremiumBracket.class)
                .matching(Query.query(where("grade").is(grade)))
                .first()
                .hasElement();
    }

    @Override
    public Mono<Long> countApplicablePensionBrackets() {
        return r2dbcEntityTemplate
                .select(PremiumBracket.class)
                .matching(Query.query(
                        where("pension").greaterThan(BigDecimal.ZERO)
                ))
                .all()
                .count();
    }
}


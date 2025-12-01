package jp.asatex.matianchi.social_insurance_backend_service.repository;

import jp.asatex.matianchi.social_insurance_backend_service.entity.PremiumBracket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 保险费等级自定义Repository接口
 * 定义需要自定义实现的复杂查询方法
 */
public interface PremiumBracketCustomRepository {

    /**
     * 根据金额范围查找所有匹配的保险费等级记录
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return Flux包装的PremiumBracket对象流
     */
    Flux<PremiumBracket> findByAmountRange(Integer minAmount, Integer maxAmount);

    /**
     * 查找所有适用厚生年金的保险费等级记录
     * @return Flux包装的PremiumBracket对象流
     */
    Flux<PremiumBracket> findApplicablePensionBrackets();

    /**
     * 根据标准报酬范围查找保险费等级记录
     * @param minStdRem 最小标准报酬
     * @param maxStdRem 最大标准报酬
     * @return Flux包装的PremiumBracket对象流
     */
    Flux<PremiumBracket> findByStdRemRange(Integer minStdRem, Integer maxStdRem);

    /**
     * 批量保存保险费等级记录
     * @param brackets 保险费等级记录集合
     * @return Flux包装的已保存的PremiumBracket对象流
     */
    Flux<PremiumBracket> saveAll(Flux<PremiumBracket> brackets);

    /**
     * 检查等级是否存在
     * @param grade 等级
     * @return Mono包装的Boolean值
     */
    Mono<Boolean> existsByGrade(String grade);

    /**
     * 统计适用厚生年金的记录数量
     * @return Mono包装的Long值
     */
    Mono<Long> countApplicablePensionBrackets();
}


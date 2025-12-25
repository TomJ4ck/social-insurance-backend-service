package jp.asatex.matianchi.social_insurance_backend_service.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jp.asatex.matianchi.social_insurance_backend_service.entity.PremiumBracket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 保险费等级响应式Repository接口
 * 提供流式编程风格的数据库操作方法
 * 继承ReactiveCrudRepository提供基础CRUD操作
 * 继承PremiumBracketCustomRepository提供自定义复杂查询操作
 */
@Repository
public interface PremiumBracketRepository extends ReactiveCrudRepository<PremiumBracket, Long>, PremiumBracketCustomRepository {

    /**
     * 根据等级查找保险费等级记录
     * @param grade 等级
     * @return Mono包装的PremiumBracket对象
     */
    Mono<PremiumBracket> findByGrade(String grade);

    /**
     * 根据金额查找对应的保险费等级记录
     * 查找minAmount <= amount <= maxAmount的记录
     * 当边界值匹配多个区间时，返回min_amount最大的区间（即更高的等级）
     * @param amount 金额
     * @return Mono包装的PremiumBracket对象（应该只有一条记录）
     */
    @Query("SELECT * FROM premium_bracket WHERE min_amount <= :amount AND max_amount >= :amount ORDER BY min_amount DESC LIMIT 1")
    Mono<PremiumBracket> findByAmount(@Param("amount") Integer amount);

    /**
     * 查找所有保险费等级记录（按标准报酬升序排序）
     * @return Flux包装的PremiumBracket对象流
     */
    @Query("SELECT * FROM premium_bracket ORDER BY std_rem ASC")
    Flux<PremiumBracket> findAllOrderByStdRemAsc();
}


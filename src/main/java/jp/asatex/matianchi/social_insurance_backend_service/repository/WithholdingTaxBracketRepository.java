package jp.asatex.matianchi.social_insurance_backend_service.repository;

import jp.asatex.matianchi.social_insurance_backend_service.entity.WithholdingTaxBracket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * 源泉征收税等级响应式Repository接口
 * 提供流式编程风格的数据库操作方法
 */
@Repository
public interface WithholdingTaxBracketRepository extends ReactiveCrudRepository<WithholdingTaxBracket, Long> {

    /**
     * 根据扣除社会保险费后的工资金额查找对应的源泉征收税等级记录
     * 查找minAmount <= amount <= maxAmount的记录
     * @param amount 扣除社会保险费后的工资金额
     * @return Mono包装的WithholdingTaxBracket对象
     */
    @Query("SELECT * FROM withholding_tax_bracket WHERE min_amount <= :amount AND max_amount >= :amount LIMIT 1")
    Mono<WithholdingTaxBracket> findByAmount(Integer amount);
}


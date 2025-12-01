package jp.asatex.matianchi.social_insurance_backend_service.repository;

import jp.asatex.matianchi.social_insurance_backend_service.entity.EmploymentInsuranceRate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * 雇佣保险费率响应式Repository接口
 * 提供流式编程风格的数据库操作方法
 */
@Repository
public interface EmploymentInsuranceRateRepository extends ReactiveCrudRepository<EmploymentInsuranceRate, Long> {

    /**
     * 根据事业类型查找雇佣保险费率记录
     * @param businessType 事业类型
     * @return Mono包装的EmploymentInsuranceRate对象
     */
    Mono<EmploymentInsuranceRate> findByBusinessType(String businessType);
}


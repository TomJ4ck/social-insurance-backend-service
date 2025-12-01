package jp.asatex.matianchi.social_insurance_backend_service.application;

import jp.asatex.matianchi.social_insurance_backend_service.application.dto.SocialInsuranceApplicationDto;
import jp.asatex.matianchi.social_insurance_backend_service.domain.PremiumBracketDomainService;
import jp.asatex.matianchi.social_insurance_backend_service.domain.dto.SocialInsuranceDomainDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 保险费等级Application Service
 * 提供应用层服务，调用Domain层方法并处理DTO转换
 * 采用流式编程风格
 */
@Service
public class PremiumBracketApplicationService {

    private final PremiumBracketDomainService domainService;

    public PremiumBracketApplicationService(PremiumBracketDomainService domainService) {
        this.domainService = domainService;
    }

    /**
     * 查询社会保险金额
     * 调用Domain层的同名方法获取数据，并将Domain DTO转换为Application DTO
     * 
     * @param monthlySalary 月薪
     * @param age 年龄
     * @return Mono包装的SocialInsuranceApplicationDto对象
     */
    public Mono<SocialInsuranceApplicationDto> socialInsuranceQuery(Integer monthlySalary, Integer age) {
        return domainService.socialInsuranceQuery(monthlySalary, age)
                .map(this::convertToApplicationDto)
                .doOnNext(dto -> {
                    // 可以在这里添加应用层逻辑，如日志记录、审计等
                });
    }

    /**
     * 将Domain DTO转换为Application DTO
     * 采用流式编程风格进行转换
     * 
     * @param domainDto Domain层的DTO
     * @return Application层的DTO
     */
    private SocialInsuranceApplicationDto convertToApplicationDto(SocialInsuranceDomainDto domainDto) {
        // 转换雇员费用
        SocialInsuranceDomainDto.CostDetail domainEmployeeCost = domainDto.getEmployeeCost();
        SocialInsuranceApplicationDto.CostDetail applicationEmployeeCost = new SocialInsuranceApplicationDto.CostDetail(
                domainEmployeeCost.getHealthCostWithNoCare(),
                domainEmployeeCost.getCareCost(),
                domainEmployeeCost.getPension(),
                domainEmployeeCost.getWithholdingTax(),
                domainEmployeeCost.getEmploymentInsurance()
        );
        
        // 转换雇主费用
        SocialInsuranceDomainDto.CostDetail domainEmployerCost = domainDto.getEmployerCost();
        SocialInsuranceApplicationDto.CostDetail applicationEmployerCost = new SocialInsuranceApplicationDto.CostDetail(
                domainEmployerCost.getHealthCostWithNoCare(),
                domainEmployerCost.getCareCost(),
                domainEmployerCost.getPension(),
                domainEmployerCost.getWithholdingTax(),
                domainEmployerCost.getEmploymentInsurance()
        );
        
        return new SocialInsuranceApplicationDto(applicationEmployeeCost, applicationEmployerCost);
    }
}


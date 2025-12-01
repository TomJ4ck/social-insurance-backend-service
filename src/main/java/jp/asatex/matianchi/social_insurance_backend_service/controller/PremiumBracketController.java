package jp.asatex.matianchi.social_insurance_backend_service.controller;

import jp.asatex.matianchi.social_insurance_backend_service.application.PremiumBracketApplicationService;
import jp.asatex.matianchi.social_insurance_backend_service.application.dto.SocialInsuranceApplicationDto;
import jp.asatex.matianchi.social_insurance_backend_service.controller.dto.SocialInsuranceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 保险费等级Controller
 * 提供RESTful API端点，采用WebFlux响应式编程风格
 */
@RestController
@RequestMapping("/")
public class PremiumBracketController {

    private final PremiumBracketApplicationService applicationService;

    public PremiumBracketController(PremiumBracketApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 查询社会保险金额
     * GET端点：/socialInsuranceQuery
     * 
     * @param monthlySalary 月薪
     * @param age 年龄
     * @return Mono包装的ResponseEntity<SocialInsuranceDto>
     */
    @GetMapping("/socialInsuranceQuery")
    public Mono<ResponseEntity<SocialInsuranceDto>> socialInsuranceQuery(
            @RequestParam("monthlySalary") Integer monthlySalary,
            @RequestParam("age") Integer age) {
        
        return applicationService.socialInsuranceQuery(monthlySalary, age)
                .map(this::convertToControllerDto)
                .map(ResponseEntity::ok);
    }

    /**
     * 将Application DTO转换为Controller DTO
     * 采用流式编程风格进行转换
     * 
     * @param applicationDto Application层的DTO
     * @return Controller层的DTO
     */
    private SocialInsuranceDto convertToControllerDto(SocialInsuranceApplicationDto applicationDto) {
        // 转换雇员费用
        SocialInsuranceApplicationDto.CostDetail applicationEmployeeCost = applicationDto.getEmployeeCost();
        SocialInsuranceDto.CostDetail controllerEmployeeCost = new SocialInsuranceDto.CostDetail(
                applicationEmployeeCost.getHealthCostWithNoCare(),
                applicationEmployeeCost.getCareCost(),
                applicationEmployeeCost.getPension(),
                applicationEmployeeCost.getWithholdingTax(),
                applicationEmployeeCost.getEmploymentInsurance()
        );
        
        // 转换雇主费用
        SocialInsuranceApplicationDto.CostDetail applicationEmployerCost = applicationDto.getEmployerCost();
        SocialInsuranceDto.CostDetail controllerEmployerCost = new SocialInsuranceDto.CostDetail(
                applicationEmployerCost.getHealthCostWithNoCare(),
                applicationEmployerCost.getCareCost(),
                applicationEmployerCost.getPension(),
                applicationEmployerCost.getWithholdingTax(),
                applicationEmployerCost.getEmploymentInsurance()
        );
        
        return new SocialInsuranceDto(controllerEmployeeCost, controllerEmployerCost);
    }
}


package jp.asatex.matianchi.social_insurance_backend_service.controller;

import jp.asatex.matianchi.social_insurance_backend_service.controller.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 全局异常处理器
 * 采用WebFlux响应式编程风格处理所有异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理IllegalArgumentException异常
     * 通常用于参数验证错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                exchange.getRequest().getPath().value()
        );
    }

    /**
     * 处理WebExchangeBindException异常
     * 用于请求参数绑定验证错误
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleWebExchangeBindException(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse(ex.getMessage());
        
        return createErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                exchange.getRequest().getPath().value(),
                errorMessage
        );
    }

    /**
     * 处理所有其他异常
     * 作为兜底异常处理器
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        return createErrorResponse(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                exchange.getRequest().getPath().value()
        );
    }

    /**
     * 创建错误响应
     * 采用流式编程风格
     * 
     * @param ex 异常对象
     * @param status HTTP状态码
     * @param error 错误类型
     * @param path 请求路径
     * @return Mono包装的ResponseEntity<ErrorResponseDto>
     */
    private Mono<ResponseEntity<ErrorResponseDto>> createErrorResponse(
            Throwable ex, HttpStatus status, String error, String path) {
        return createErrorResponse(ex, status, error, path, ex.getMessage());
    }

    /**
     * 创建错误响应（带自定义消息）
     * 采用流式编程风格
     * 
     * @param ex 异常对象
     * @param status HTTP状态码
     * @param error 错误类型
     * @param path 请求路径
     * @param message 自定义错误消息
     * @return Mono包装的ResponseEntity<ErrorResponseDto>
     */
    private Mono<ResponseEntity<ErrorResponseDto>> createErrorResponse(
            Throwable ex, HttpStatus status, String error, String path, String message) {
        
        // 输出异常消息到控制台
        System.err.println("异常发生: " + ex.getClass().getSimpleName());
        System.err.println("异常消息: " + message);
        System.err.println("请求路径: " + path);
        if (ex.getCause() != null) {
            System.err.println("根本原因: " + ex.getCause().getMessage());
        }
        ex.printStackTrace();
        
        // 创建错误响应DTO
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                error,
                message != null ? message : "发生未知错误",
                path
        );
        
        // 返回响应
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
}


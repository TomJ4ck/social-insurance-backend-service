package jp.asatex.matianchi.social_insurance_backend_service.controller.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 错误响应DTO
 * 用于全局异常处理器返回错误信息
 */
public class ErrorResponseDto {

    /**
     * 错误时间时间戳
     */
    private LocalDateTime timestamp;

    /**
     * HTTP状态码
     */
    private Integer status;

    /**
     * 错误类型
     */
    private String error;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 请求路径
     */
    private String path;

    // 默认构造函数
    public ErrorResponseDto() {
    }

    // 全参构造函数
    public ErrorResponseDto(LocalDateTime timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getter和Setter方法
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponseDto that = (ErrorResponseDto) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(status, that.status) &&
               Objects.equals(error, that.error) &&
               Objects.equals(message, that.message) &&
               Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, status, error, message, path);
    }

    @Override
    public String toString() {
        return "ErrorResponseDto{" +
               "timestamp=" + timestamp +
               ", status=" + status +
               ", error='" + error + '\'' +
               ", message='" + message + '\'' +
               ", path='" + path + '\'' +
               '}';
    }
}


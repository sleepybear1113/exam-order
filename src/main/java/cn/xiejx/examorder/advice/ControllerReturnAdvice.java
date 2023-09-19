package cn.xiejx.examorder.advice;


import cn.xiejx.examorder.exception.FrontException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author XJX
 * @date 2021/8/10 1:59
 */
@RestControllerAdvice(basePackages = {"cn.xiejx.examorder.controller"})
@Slf4j
public class ControllerReturnAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class<? extends HttpMessageConverter<?>> aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
        if (o instanceof ResultCode) {
            return o;
        }

        return new ResultCode<>(o);
    }

    @ExceptionHandler(value = FrontException.class)
    public <T> ResultCode<T> handlerGlobeMyException(HttpServletRequest request, FrontException exception) {
        log.warn("用户错误：" + exception.getMessage());
        return new ResultCode<>(ResultCodeConstant.CodeEnum.COMMON_ERROR.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(value = Exception.class)
    public <T> ResultCode<T> handlerGlobeException(HttpServletRequest request, Exception exception) {
        log.error("系统错误：" + exception.getMessage(), exception);
        return new ResultCode<>(ResultCodeConstant.CodeEnum.SYSTEM_ERROR.getCode(), exception.getMessage(), null);
    }
}
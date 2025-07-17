package Integration.integration.exception;

import Integration.integration.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", msg));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustom(CustomException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnknown(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }
}

